<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="需求标题" prop="demandTitle">
        <el-input v-model="queryParams.demandTitle" placeholder="请输入需求标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" filterable clearable>
          <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="demandStatus">
        <el-select v-model="queryParams.demandStatus" placeholder="请选择状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:demand:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:demand:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:demand:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="demandList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="需求ID" align="center" prop="demandId" width="90" />
      <el-table-column label="需求标题" align="center" prop="demandTitle" :show-overflow-tooltip="true" />
      <el-table-column label="买家" align="center" prop="userId" width="100">
        <template slot-scope="scope">{{ scope.row.userName || userName(scope.row.userId) }}</template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryId" width="120">
        <template slot-scope="scope">{{ categoryName(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="预算" align="center" prop="budgetAmount" width="110" />
      <el-table-column label="状态" align="center" prop="demandStatus" width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTag(scope.row.demandStatus)">{{ statusText(scope.row.demandStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:demand:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:demand:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="需求标题" prop="demandTitle">
          <el-input v-model="form.demandTitle" placeholder="如 定制手绘团扇" />
        </el-form-item>
        <el-form-item label="买家" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择买家" filterable clearable>
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName || u.userName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" filterable clearable>
            <el-option v-for="item in categoryOptions" :key="item.categoryId" :label="item.categoryName" :value="item.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额" prop="budgetAmount">
          <el-input-number v-model="form.budgetAmount" :min="0" :precision="2" :step="50" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="demandStatus">
          <el-select v-model="form.demandStatus" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="需求说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入定制尺寸、材质、交付要求等" />
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
import { listDemand, getDemand, delDemand, addDemand, updateDemand } from '@/api/creative/demand'
import { listCategory } from '@/api/creative/category'
import { listUser } from '@/api/system/user'

export default {
  name: 'CreativeDemand',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      demandList: [],
      categoryOptions: [],
      userOptions: [],
      statusOptions: [
        { label: '草稿', value: 'draft', tag: 'info' },
        { label: '待报价', value: 'published', tag: 'warning' },
        { label: '已接单', value: 'accepted', tag: 'success' },
        { label: '已关闭', value: 'closed', tag: 'info' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        demandTitle: undefined,
        categoryId: undefined,
        demandStatus: undefined
      },
      form: {},
      rules: {
        demandTitle: [{ required: true, message: '需求标题不能为空', trigger: 'blur' }],
        userId: [{ required: true, message: '买家ID不能为空', trigger: 'blur' }],
        categoryId: [{ required: true, message: '分类不能为空', trigger: 'change' }],
        budgetAmount: [{ required: true, message: '预算金额不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryOptions()
    this.getUserOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listDemand(this.queryParams).then(response => {
        this.demandList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getCategoryOptions() {
      listCategory({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
        this.categoryOptions = response.rows || []
      })
    },
    getUserOptions() {
      listUser({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.userOptions = response.rows || []
      })
    },
    userName(id) {
      const u = this.userOptions.find(i => i.userId === id)
      return u ? (u.nickName || u.userName) : id
    },
    categoryName(id) {
      const c = this.categoryOptions.find(i => i.categoryId === id)
      return c ? c.categoryName : id
    },
    statusText(value) {
      const item = this.statusOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    statusTag(value) {
      const item = this.statusOptions.find(option => option.value === value)
      return item ? item.tag : 'info'
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        demandId: undefined,
        userId: undefined,
        categoryId: undefined,
        demandTitle: undefined,
        budgetAmount: 0,
        demandStatus: 'published',
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
      this.ids = selection.map(item => item.demandId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增定制需求'
    },
    handleUpdate(row) {
      this.reset()
      const demandId = row.demandId || this.ids
      getDemand(demandId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改定制需求'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.demandId !== undefined) {
            updateDemand(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addDemand(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const demandIds = row.demandId || this.ids
      this.$modal.confirm('是否确认删除需求编号为"' + demandIds + '"的数据项？').then(function() {
        return delDemand(demandIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
