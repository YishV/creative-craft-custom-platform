<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="订单号">
        <el-input v-model="queryParams.orderNo" placeholder="搜索订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.orderStatus" placeholder="全部状态" clearable>
          <el-option label="已创建" value="created" />
          <el-option label="制作中" value="making" />
          <el-option label="已发货" value="shipped" />
          <el-option label="已完成" value="finished" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="orders" class="portal-table">
      <el-table-column label="订单号" prop="orderNo" min-width="180" />
      <el-table-column label="金额" width="130">
        <template slot-scope="scope">￥{{ money(scope.row.orderAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="statusMeta(scope.row.orderStatus).type" size="mini">{{ statusMeta(scope.row.orderStatus).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="买家/卖家" width="160">
        <template slot-scope="scope">#{{ scope.row.buyerId || '-' }} / #{{ scope.row.sellerId || '-' }}</template>
      </el-table-column>
      <el-table-column label="说明" prop="remark" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="230" align="center">
        <template slot-scope="scope">
          <el-button v-if="scope.row.orderStatus === 'created'" type="text" @click="start(scope.row)">开始制作</el-button>
          <el-button v-if="scope.row.orderStatus === 'making'" type="text" @click="ship(scope.row)">发货</el-button>
          <el-button v-if="scope.row.orderStatus === 'shipped'" type="text" @click="finish(scope.row)">确认收货</el-button>
          <el-button v-if="['created', 'making'].includes(scope.row.orderStatus)" type="text" class="danger" @click="cancel(scope.row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listOrder, startOrder, shipOrder, finishOrder, cancelOrder } from '@/api/creative/order'

const STATUS_MAP = {
  created: { label: '已创建', type: 'info' },
  making: { label: '制作中', type: 'warning' },
  shipped: { label: '已发货', type: 'primary' },
  finished: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'danger' }
}

export default {
  name: 'PortalOrders',
  data() {
    return {
      loading: false,
      orders: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: undefined,
        orderStatus: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(res => {
        this.orders = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.orderNo = undefined
      this.queryParams.orderStatus = undefined
      this.handleQuery()
    },
    statusMeta(status) {
      return STATUS_MAP[status] || { label: status || '-', type: 'info' }
    },
    start(row) {
      startOrder(row.orderId).then(() => {
        this.$modal.msgSuccess('已开始制作')
        this.getList()
      })
    },
    ship(row) {
      shipOrder(row.orderId).then(() => {
        this.$modal.msgSuccess('已发货')
        this.getList()
      })
    },
    finish(row) {
      finishOrder(row.orderId).then(() => {
        this.$modal.msgSuccess('订单已完成')
        this.getList()
      })
    },
    cancel(row) {
      this.$modal.confirm(`确认取消订单“${row.orderNo}”？`).then(() => {
        return cancelOrder(row.orderId)
      }).then(() => {
        this.$modal.msgSuccess('订单已取消')
        this.getList()
      }).catch(() => {})
    },
    money(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.portal-page {
  padding: 20px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.toolbar,
.portal-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.toolbar {
  padding: 16px 16px 0;
  margin-bottom: 16px;
}

.danger {
  color: #f56c6c;
}
</style>
