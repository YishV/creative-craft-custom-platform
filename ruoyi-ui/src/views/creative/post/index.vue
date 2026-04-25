<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="标题" prop="postTitle">
        <el-input v-model="queryParams.postTitle" placeholder="请输入作品标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创作者" prop="creatorId">
        <el-select v-model="queryParams.creatorId" placeholder="请选择创作者" filterable clearable>
          <el-option v-for="c in creatorOptions" :key="c.creatorId" :label="c.creatorName" :value="c.creatorId" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="postType">
        <el-select v-model="queryParams.postType" placeholder="请选择类型" clearable>
          <el-option label="作品" value="work" />
          <el-option label="教程" value="tutorial" />
          <el-option label="动态" value="news" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="发布" value="0" />
          <el-option label="下架" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:post:add']">发布</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:post:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:post:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="作品ID" align="center" prop="postId" width="80" />
      <el-table-column label="标题" align="center" prop="postTitle" :show-overflow-tooltip="true" />
      <el-table-column label="创作者" align="center" prop="creatorId" width="140">
        <template slot-scope="scope">{{ creatorName(scope.row.creatorId) }}</template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="postType" width="90">
        <template slot-scope="scope">{{ typeText(scope.row.postType) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">{{ scope.row.status === '0' ? '发布' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:post:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:post:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="postTitle">
          <el-input v-model="form.postTitle" placeholder="请输入作品标题" />
        </el-form-item>
        <el-form-item label="创作者" prop="creatorId">
          <el-select v-model="form.creatorId" placeholder="请选择创作者" filterable>
            <el-option v-for="c in creatorOptions" :key="c.creatorId" :label="c.creatorName" :value="c.creatorId" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="postType">
          <el-radio-group v-model="form.postType">
            <el-radio label="work">作品</el-radio>
            <el-radio label="tutorial">教程</el-radio>
            <el-radio label="news">动态</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">发布</el-radio>
            <el-radio label="1">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPost, getPost, delPost, addPost, updatePost } from '@/api/creative/post'
import { listCreator } from '@/api/creative/creator'

const TYPE_MAP = { work: '作品', tutorial: '教程', news: '动态' }

export default {
  name: 'CreativePost',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      postList: [],
      creatorOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postTitle: undefined,
        creatorId: undefined,
        postType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        postTitle: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        creatorId: [{ required: true, message: '创作者不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCreatorOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listPost(this.queryParams).then(response => {
        this.postList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getCreatorOptions() {
      listCreator({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
        this.creatorOptions = response.rows || []
      })
    },
    creatorName(id) {
      const c = this.creatorOptions.find(i => i.creatorId === id)
      return c ? c.creatorName : id
    },
    typeText(t) {
      return TYPE_MAP[t] || t || '-'
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        postId: undefined,
        creatorId: undefined,
        postTitle: undefined,
        postType: 'work',
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.postId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '发布作品'
    },
    handleUpdate(row) {
      this.reset()
      const postId = row.postId || this.ids
      getPost(postId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改作品'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.postId !== undefined) {
            updatePost(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addPost(this.form).then(() => {
              this.$modal.msgSuccess('发布成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const postIds = row.postId || this.ids
      this.$modal.confirm('是否确认删除作品编号为"' + postIds + '"的数据项？').then(function() {
        return delPost(postIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
