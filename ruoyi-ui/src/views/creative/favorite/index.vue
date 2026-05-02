<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户ID" prop="userId">
        <el-input-number v-model="queryParams.userId" :min="1" controls-position="right" />
      </el-form-item>
      <el-form-item label="目标类型" prop="targetType">
        <el-select v-model="queryParams.targetType" placeholder="请选择类型" clearable>
          <el-option label="商品" value="product" />
          <el-option label="创作者" value="creator" />
          <el-option label="作品" value="post" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="有效" value="0" />
          <el-option label="取消" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:favorite:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:favorite:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:favorite:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="favoriteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="收藏ID" align="center" prop="favoriteId" width="80" />
      <el-table-column label="收藏人" align="center" prop="userName" width="120">
        <template slot-scope="scope">{{ scope.row.userName || (scope.row.userId ? `#${scope.row.userId}` : '-') }}</template>
      </el-table-column>
      <el-table-column label="目标类型" align="center" prop="targetType" width="120">
        <template slot-scope="scope">
          <el-tag size="mini" :type="targetTagType(scope.row.targetType)">{{ targetTypeText(scope.row.targetType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标" align="center" prop="targetId" :show-overflow-tooltip="true">
        <template slot-scope="scope">{{ scope.row.targetName || targetLabel(scope.row.targetType, scope.row.targetId) }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="mini">{{ scope.row.status === '0' ? '有效' : '取消' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收藏时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:favorite:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:favorite:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="form.userId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="目标类型" prop="targetType">
          <el-radio-group v-model="form.targetType" @change="onTargetTypeChange">
            <el-radio label="product">商品</el-radio>
            <el-radio label="creator">创作者</el-radio>
            <el-radio label="post">作品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="目标" prop="targetId">
          <el-select v-model="form.targetId" placeholder="请选择目标" filterable v-if="form.targetType">
            <el-option v-for="opt in targetOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
          <el-input-number v-else v-model="form.targetId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">有效</el-radio>
            <el-radio label="1">取消</el-radio>
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
import { listFavorite, getFavorite, delFavorite, addFavorite, updateFavorite } from '@/api/creative/favorite'
import { listProduct } from '@/api/creative/product'
import { listCreator } from '@/api/creative/creator'
import { listPost } from '@/api/creative/post'

const TYPE_MAP = { product: '商品', creator: '创作者', post: '作品' }
const TYPE_TAG = { product: 'success', creator: 'warning', post: '' }

export default {
  name: 'CreativeFavorite',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      favoriteList: [],
      productOptions: [],
      creatorOptions: [],
      postOptions: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: undefined,
        targetType: undefined,
        status: undefined
      },
      form: {},
      rules: {
        userId: [{ required: true, message: '用户ID不能为空', trigger: 'blur' }],
        targetType: [{ required: true, message: '目标类型不能为空', trigger: 'change' }],
        targetId: [{ required: true, message: '目标不能为空', trigger: 'change' }]
      }
    }
  },
  computed: {
    targetOptions() {
      if (this.form.targetType === 'product') {
        return this.productOptions.map(p => ({ value: p.productId, label: p.productName }))
      }
      if (this.form.targetType === 'creator') {
        return this.creatorOptions.map(c => ({ value: c.creatorId, label: c.creatorName }))
      }
      if (this.form.targetType === 'post') {
        return this.postOptions.map(p => ({ value: p.postId, label: p.postTitle }))
      }
      return []
    }
  },
  created() {
    this.getList()
    this.loadOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listFavorite(this.queryParams).then(response => {
        this.favoriteList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    loadOptions() {
      listProduct({ pageNum: 1, pageSize: 1000 }).then(r => { this.productOptions = r.rows || [] })
      listCreator({ pageNum: 1, pageSize: 1000 }).then(r => { this.creatorOptions = r.rows || [] })
      listPost({ pageNum: 1, pageSize: 1000 }).then(r => { this.postOptions = r.rows || [] })
    },
    targetTypeText(t) {
      return TYPE_MAP[t] || t || '-'
    },
    targetTagType(t) {
      return TYPE_TAG[t] || 'info'
    },
    targetLabel(type, id) {
      if (type === 'product') {
        const p = this.productOptions.find(i => i.productId === id)
        return p ? p.productName : (id ? `#${id}` : '-')
      }
      if (type === 'creator') {
        const c = this.creatorOptions.find(i => i.creatorId === id)
        return c ? (c.storeName || c.creatorName) : (id ? `#${id}` : '-')
      }
      if (type === 'post') {
        const p = this.postOptions.find(i => i.postId === id)
        return p ? p.postTitle : (id ? `#${id}` : '-')
      }
      return id ? `#${id}` : '-'
    },
    onTargetTypeChange() {
      this.form.targetId = undefined
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        favoriteId: undefined,
        userId: undefined,
        targetType: 'product',
        targetId: undefined,
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
      this.ids = selection.map(item => item.favoriteId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增收藏'
    },
    handleUpdate(row) {
      this.reset()
      const favoriteId = row.favoriteId || this.ids
      getFavorite(favoriteId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改收藏'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.favoriteId !== undefined) {
            updateFavorite(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addFavorite(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const favoriteIds = row.favoriteId || this.ids
      this.$modal.confirm('是否确认删除收藏编号为"' + favoriteIds + '"的数据项？').then(function() {
        return delFavorite(favoriteIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
