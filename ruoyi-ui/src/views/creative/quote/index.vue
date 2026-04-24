<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="需求ID" prop="demandId">
        <el-input-number v-model="queryParams.demandId" :min="1" controls-position="right" />
      </el-form-item>
      <el-form-item label="创作者ID" prop="creatorId">
        <el-input-number v-model="queryParams.creatorId" :min="1" controls-position="right" />
      </el-form-item>
      <el-form-item label="状态" prop="quoteStatus">
        <el-select v-model="queryParams.quoteStatus" placeholder="请选择状态" clearable>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:quote:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:quote:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:quote:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="quoteList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报价ID" align="center" prop="quoteId" width="90" />
      <el-table-column label="需求ID" align="center" prop="demandId" width="90" />
      <el-table-column label="创作者ID" align="center" prop="creatorId" width="100" />
      <el-table-column label="报价金额" align="center" prop="quoteAmount" width="110" />
      <el-table-column label="交付天数" align="center" prop="deliveryDays" width="100" />
      <el-table-column label="状态" align="center" prop="quoteStatus" width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTag(scope.row.quoteStatus)">{{ statusText(scope.row.quoteStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:quote:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:quote:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="需求ID" prop="demandId">
          <el-input-number v-model="form.demandId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="创作者ID" prop="creatorId">
          <el-input-number v-model="form.creatorId" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="报价金额" prop="quoteAmount">
          <el-input-number v-model="form.quoteAmount" :min="0" :precision="2" :step="50" controls-position="right" />
        </el-form-item>
        <el-form-item label="交付天数" prop="deliveryDays">
          <el-input-number v-model="form.deliveryDays" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="quoteStatus">
          <el-select v-model="form.quoteStatus" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报价说明" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入材料、工期、沟通说明" />
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
import { listQuote, getQuote, delQuote, addQuote, updateQuote } from '@/api/creative/quote'

export default {
  name: 'CreativeQuote',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      quoteList: [],
      statusOptions: [
        { label: '待确认', value: 'pending', tag: 'warning' },
        { label: '已选中', value: 'selected', tag: 'success' },
        { label: '已拒绝', value: 'rejected', tag: 'danger' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        demandId: undefined,
        creatorId: undefined,
        quoteStatus: undefined
      },
      form: {},
      rules: {
        demandId: [{ required: true, message: '需求ID不能为空', trigger: 'blur' }],
        creatorId: [{ required: true, message: '创作者ID不能为空', trigger: 'blur' }],
        quoteAmount: [{ required: true, message: '报价金额不能为空', trigger: 'blur' }],
        deliveryDays: [{ required: true, message: '交付天数不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listQuote(this.queryParams).then(response => {
        this.quoteList = response.rows
        this.total = response.total
        this.loading = false
      })
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
        quoteId: undefined,
        demandId: undefined,
        creatorId: undefined,
        quoteAmount: 0,
        deliveryDays: 7,
        quoteStatus: 'pending',
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
      this.ids = selection.map(item => item.quoteId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增定制报价'
    },
    handleUpdate(row) {
      this.reset()
      const quoteId = row.quoteId || this.ids
      getQuote(quoteId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改定制报价'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.quoteId !== undefined) {
            updateQuote(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addQuote(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const quoteIds = row.quoteId || this.ids
      this.$modal.confirm('是否确认删除报价编号为"' + quoteIds + '"的数据项？').then(function() {
        return delQuote(quoteIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
