<template>
  <div class="portal-page">
    <section class="payment-panel">
      <div class="pay-card">
        <i class="el-icon-bank-card icon"></i>
        <h2>模拟支付</h2>
        <p>订单 {{ orderIds.length ? orderIds.map(id => '#' + id).join('、') : '-' }}</p>
        <strong>￥{{ money(amount) }}</strong>
        <div class="fake-code">
          <span></span><span></span><span></span><span></span>
          <span></span><span></span><span></span><span></span>
          <span></span><span></span><span></span><span></span>
          <span></span><span></span><span></span><span></span>
        </div>
        <p>请完成演示支付后返回订单中心查看最新状态。</p>
        <el-button type="primary" :loading="loading" :disabled="!orderIds.length" @click="pay">确认已支付</el-button>
        <el-button type="text" @click="$router.push('/portal/orders')">返回订单中心</el-button>
      </div>
    </section>
  </div>
</template>

<script>
import { payPortalOrder } from '@/api/creative/portal'

export default {
  name: 'PortalPayment',
  data() {
    return {
      loading: false,
      orderIds: String(this.$route.query.orderIds || this.$route.query.orderId || '').split(',').filter(Boolean),
      amount: this.$route.query.amount
    }
  },
  methods: {
    pay() {
      this.loading = true
      Promise.all(this.orderIds.map(orderId => payPortalOrder(orderId))).then(() => {
        this.$modal.msgSuccess('模拟支付成功')
        this.$router.push('/portal/orders')
      }).finally(() => {
        this.loading = false
      })
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

.payment-panel {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

.pay-card {
  width: 360px;
  padding: 28px;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.icon {
  color: #287b76;
  font-size: 42px;
}

.pay-card h2 {
  margin: 14px 0 8px;
  font-size: 22px;
}

.pay-card p {
  margin: 0 0 10px;
  color: #6b7280;
  line-height: 1.6;
}

.pay-card strong {
  color: #d97706;
  font-size: 28px;
}

.fake-code {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  width: 160px;
  height: 160px;
  margin: 22px auto;
  padding: 14px;
  border: 1px solid #d8e2dc;
}

.fake-code span {
  background: #1f2937;
}

.fake-code span:nth-child(3n) {
  background: #d97706;
}
</style>
