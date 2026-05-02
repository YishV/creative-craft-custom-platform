<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单编号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择状态" clearable>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['creative:order:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['creative:order:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['creative:order:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" width="90" />
      <el-table-column label="订单编号" align="center" prop="orderNo" :show-overflow-tooltip="true" />
      <el-table-column label="买家" align="center" prop="buyerId" width="120">
        <template slot-scope="scope">{{ scope.row.buyerName || userName(scope.row.buyerId) }}</template>
      </el-table-column>
      <el-table-column label="卖家(创作者)" align="center" prop="sellerId" width="140">
        <template slot-scope="scope">{{ scope.row.sellerName || creatorName(scope.row.sellerId) }}</template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="orderAmount" width="110" />
      <el-table-column label="状态" align="center" prop="orderStatus" width="110">
        <template slot-scope="scope">
          <el-tag size="mini" :type="statusTag(scope.row.orderStatus)">{{ statusText(scope.row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="320">
        <template slot-scope="scope">
          <el-button size="mini" type="text" v-if="scope.row.orderStatus === 'created'" @click="handleTransit(scope.row, 'start')" v-hasPermi="['creative:order:edit']">开始制作</el-button>
          <el-button size="mini" type="text" v-if="scope.row.orderStatus === 'making'" @click="handleTransit(scope.row, 'ship')" v-hasPermi="['creative:order:edit']">发货</el-button>
          <el-button size="mini" type="text" v-if="scope.row.orderStatus === 'shipped'" @click="handleTransit(scope.row, 'finish')" v-hasPermi="['creative:order:edit']">完成</el-button>
          <el-button size="mini" type="text" v-if="scope.row.orderStatus === 'created' || scope.row.orderStatus === 'making'" @click="handleTransit(scope.row, 'cancel')" v-hasPermi="['creative:order:edit']">取消</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['creative:order:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['creative:order:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="540px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="为空时请手动填写，如 CRAFT20260424001" />
        </el-form-item>
        <el-form-item label="买家" prop="buyerId">
          <el-select v-model="form.buyerId" placeholder="请选择买家" filterable>
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName || u.userName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="卖家(创作者)" prop="sellerId">
          <el-select v-model="form.sellerId" placeholder="请选择创作者" filterable>
            <el-option v-for="c in creatorOptions" :key="c.creatorId" :label="c.creatorName" :value="c.creatorId" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单金额" prop="orderAmount">
          <el-input-number v-model="form.orderAmount" :min="0" :precision="2" :step="50" controls-position="right" />
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入订单说明" />
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
import { listOrder, getOrder, delOrder, addOrder, updateOrder, startOrder, shipOrder, finishOrder, cancelOrder } from '@/api/creative/order'
import { listUser } from '@/api/system/user'
import { listCreator } from '@/api/creative/creator'

export default {
  name: 'CreativeOrder',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      orderList: [],
      userOptions: [],
      creatorOptions: [],
      statusOptions: [
        { label: '已创建', value: 'created', tag: 'info' },
        { label: '制作中', value: 'making', tag: 'warning' },
        { label: '已发货', value: 'shipped', tag: 'warning' },
        { label: '已完成', value: 'finished', tag: 'success' },
        { label: '已取消', value: 'cancelled', tag: 'danger' }
      ],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: undefined,
        orderStatus: undefined
      },
      form: {},
      rules: {
        orderNo: [{ required: true, message: '订单编号不能为空', trigger: 'blur' }],
        buyerId: [{ required: true, message: '买家ID不能为空', trigger: 'blur' }],
        sellerId: [{ required: true, message: '卖家ID不能为空', trigger: 'blur' }],
        orderAmount: [{ required: true, message: '订单金额不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.loadOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    loadOptions() {
      listUser({ pageNum: 1, pageSize: 1000 }).then(r => { this.userOptions = r.rows || [] })
      listCreator({ pageNum: 1, pageSize: 1000, status: '0' }).then(r => { this.creatorOptions = r.rows || [] })
    },
    userName(id) {
      const u = this.userOptions.find(i => i.userId === id)
      return u ? (u.nickName || u.userName) : id
    },
    creatorName(id) {
      const c = this.creatorOptions.find(i => i.creatorId === id)
      return c ? c.creatorName : id
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
        orderId: undefined,
        orderNo: 'CRAFT' + new Date().getTime(),
        buyerId: undefined,
        sellerId: undefined,
        orderAmount: 0,
        orderStatus: 'created',
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
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增定制订单'
    },
    handleUpdate(row) {
      this.reset()
      const orderId = row.orderId || this.ids
      getOrder(orderId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改定制订单'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.orderId !== undefined) {
            updateOrder(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addOrder(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const orderIds = row.orderId || this.ids
      this.$modal.confirm('是否确认删除订单编号为"' + orderIds + '"的数据项？').then(function() {
        return delOrder(orderIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleTransit(row, action) {
      const labelMap = { start: '开始制作', ship: '发货', finish: '完成', cancel: '取消' }
      const fnMap = { start: startOrder, ship: shipOrder, finish: finishOrder, cancel: cancelOrder }
      this.$modal.confirm('确认对订单 ' + row.orderNo + ' 执行 [' + labelMap[action] + ']？').then(() => {
        return fnMap[action](row.orderId)
      }).then(() => {
        this.$modal.msgSuccess(labelMap[action] + '成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>
