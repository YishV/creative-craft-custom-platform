<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属作品" prop="postId">
        <el-select v-model="queryParams.postId" placeholder="请选择作品" filterable clearable>
          <el-option v-for="p in postOptions" :key="p.postId" :label="p.postTitle" :value="p.postId" />
        </el-select>
      </el-form-item>
      <el-form-item label="评论用户" prop="userId">
        <el-input-number v-model="queryParams.userId" :min="1" controls-position="right" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="请选择状态" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="通过" value="1" />
          <el-option label="驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:comment:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:comment:edit']">审核</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:comment:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评论ID" align="center" prop="commentId" width="80" />
      <el-table-column label="所属作品" align="center" prop="postId" width="180">
        <template slot-scope="scope">{{ postTitle(scope.row.postId) }}</template>
      </el-table-column>
      <el-table-column label="评论用户" align="center" prop="userId" width="100" />
      <el-table-column label="评论内容" align="center" prop="commentContent" :show-overflow-tooltip="true" />
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="auditTagType(scope.row.auditStatus)" size="mini">{{ auditText(scope.row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评论时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:comment:edit']">审核</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:comment:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属作品" prop="postId">
          <el-select v-model="form.postId" placeholder="请选择作品" filterable>
            <el-option v-for="p in postOptions" :key="p.postId" :label="p.postTitle" :value="p.postId" />
          </el-select>
        </el-form-item>
        <el-form-item label="评论用户" prop="userId">
          <el-input-number v-model="form.userId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="评论内容" prop="commentContent">
          <el-input v-model="form.commentContent" type="textarea" :rows="3" placeholder="请输入评论内容" />
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="form.auditStatus">
            <el-radio label="0">待审核</el-radio>
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">驳回</el-radio>
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
import { listComment, getComment, delComment, addComment, updateComment } from '@/api/creative/comment'
import { listPost } from '@/api/creative/post'

const AUDIT_MAP = { '0': '待审核', '1': '通过', '2': '驳回' }
const AUDIT_TAG = { '0': 'warning', '1': 'success', '2': 'danger' }

export default {
  name: 'CreativeComment',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      commentList: [],
      postOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        postId: undefined,
        userId: undefined,
        auditStatus: undefined
      },
      form: {},
      rules: {
        postId: [{ required: true, message: '所属作品不能为空', trigger: 'change' }],
        userId: [{ required: true, message: '评论用户不能为空', trigger: 'blur' }],
        commentContent: [{ required: true, message: '评论内容不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getPostOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listComment(this.queryParams).then(response => {
        this.commentList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getPostOptions() {
      listPost({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
        this.postOptions = response.rows || []
      })
    },
    postTitle(id) {
      const p = this.postOptions.find(i => i.postId === id)
      return p ? p.postTitle : id
    },
    auditText(s) {
      return AUDIT_MAP[s] || '-'
    },
    auditTagType(s) {
      return AUDIT_TAG[s] || 'info'
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        commentId: undefined,
        postId: undefined,
        userId: undefined,
        commentContent: undefined,
        auditStatus: '0',
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
      this.ids = selection.map(item => item.commentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增评论'
    },
    handleUpdate(row) {
      this.reset()
      const commentId = row.commentId || this.ids
      getComment(commentId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '审核评论'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.commentId !== undefined) {
            updateComment(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addComment(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const commentIds = row.commentId || this.ids
      this.$modal.confirm('是否确认删除评论编号为"' + commentIds + '"的数据项？').then(function() {
        return delComment(commentIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
