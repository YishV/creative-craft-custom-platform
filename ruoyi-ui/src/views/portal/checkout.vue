<template>
  <div class="portal-page">
    <div class="page-head">
      <div>
        <h2>订单结算</h2>
        <p>确认商品和收货信息，下一步进入模拟支付。</p>
      </div>
      <el-button @click="$router.push('/portal/cart')">返回购物车</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :md="15">
        <el-card shadow="never">
          <div slot="header">商品清单</div>
          <el-table :data="items">
            <el-table-column label="商品" prop="productName" min-width="180" />
            <el-table-column label="单价" width="110">
              <template slot-scope="scope">￥{{ money(scope.row.price) }}</template>
            </el-table-column>
            <el-table-column label="数量" prop="quantity" width="90" />
            <el-table-column label="小计" width="120">
              <template slot-scope="scope">￥{{ money(scope.row.price * scope.row.quantity) }}</template>
            </el-table-column>
          </el-table>
          <div class="summary">
            <span>共 {{ totalCount }} 件</span>
            <strong>合计：￥{{ money(totalAmount) }}</strong>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="9">
        <el-card shadow="never">
          <div slot="header">收货信息</div>
          <el-form ref="addressForm" :model="address" :rules="rules" label-width="82px">
            <el-form-item label="收货人" prop="receiverName">
              <el-input v-model="address.receiverName" placeholder="请输入收货人" />
            </el-form-item>
            <el-form-item label="手机号" prop="receiverPhone">
              <el-input v-model="address.receiverPhone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="地址" prop="receiverAddress">
              <el-input v-model="address.receiverAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
            </el-form-item>
            <el-button type="primary" :loading="submitting" :disabled="!items.length" class="submit" @click="submitOrder">
              提交订单
            </el-button>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { createPortalProductOrder } from '@/api/creative/portal'
import { clearCartItems, getCartItems, getSavedAddress, saveAddress } from '@/utils/portalCart'

export default {
  name: 'PortalCheckout',
  data() {
    return {
      items: [],
      submitting: false,
      address: {
        receiverName: '',
        receiverPhone: '',
        receiverAddress: ''
      },
      rules: {
        receiverName: [{ required: true, message: '请填写收货人', trigger: 'blur' }],
        receiverPhone: [{ required: true, message: '请填写手机号', trigger: 'blur' }],
        receiverAddress: [{ required: true, message: '请填写详细地址', trigger: 'blur' }]
      }
    }
  },
  computed: {
    totalCount() {
      return this.items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
    },
    totalAmount() {
      return this.items.reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0)
    }
  },
  created() {
    this.items = getCartItems()
    this.address = Object.assign(this.address, getSavedAddress())
  },
  methods: {
    submitOrder() {
      this.$refs.addressForm.validate(valid => {
        if (!valid) return
        if (!this.items.length) {
          this.$modal.msgWarning('购物车为空')
          return
        }
        this.submitting = true
        saveAddress(this.address)
        Promise.all(this.items.map(item => createPortalProductOrder({
          productId: item.productId,
          quantity: item.quantity,
          receiverName: this.address.receiverName,
          receiverPhone: this.address.receiverPhone,
          receiverAddress: this.address.receiverAddress
        }))).then(results => {
          clearCartItems()
          const orders = results.map(res => res.data || {}).filter(order => order.orderId)
          this.$router.push({
            path: '/portal/payment',
            query: {
              orderIds: orders.map(order => order.orderId).join(','),
              amount: this.totalAmount
            }
          })
        }).finally(() => {
          this.submitting = false
        })
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

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 18px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.page-head h2 {
  margin: 0 0 6px;
  font-size: 20px;
}

.page-head p {
  margin: 0;
  color: #6b7280;
}

.summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 16px;
}

.summary strong {
  color: #d97706;
  font-size: 18px;
}

.submit {
  width: 100%;
}
</style>
