<template>
  <div class="portal-page">
    <div class="page-head">
      <div>
        <h2>购物车</h2>
        <p>确认商品和数量后即可进入结算流程。</p>
      </div>
      <el-button type="primary" :disabled="!items.length" @click="$router.push('/portal/checkout')">去结算</el-button>
    </div>

    <el-empty v-if="!items.length" description="购物车暂无商品">
      <el-button type="primary" @click="$router.push('/portal/products')">去逛商品</el-button>
    </el-empty>

    <el-table v-else :data="items" class="portal-table">
      <el-table-column label="商品" min-width="220">
        <template slot-scope="scope">
          <strong>{{ scope.row.productName }}</strong>
          <div class="muted">创作者 #{{ scope.row.creatorId || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="120">
        <template slot-scope="scope">￥{{ money(scope.row.price) }}</template>
      </el-table-column>
      <el-table-column label="数量" width="180">
        <template slot-scope="scope">
          <el-input-number v-model="scope.row.quantity" :min="1" :max="99" size="mini" @change="save" />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="140">
        <template slot-scope="scope">￥{{ money(scope.row.price * scope.row.quantity) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-button type="text" class="danger" @click="remove(scope.row.productId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="items.length" class="summary">
      <span>共 {{ totalCount }} 件</span>
      <strong>合计：￥{{ money(totalAmount) }}</strong>
    </div>
  </div>
</template>

<script>
import { getCartItems, saveCartItems, removeCartItem } from '@/utils/portalCart'

export default {
  name: 'PortalCart',
  data() {
    return {
      items: []
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
  },
  methods: {
    save() {
      saveCartItems(this.items)
    },
    remove(productId) {
      this.items = removeCartItem(productId)
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

.page-head,
.summary,
.portal-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 18px;
}

.page-head h2 {
  margin: 0 0 6px;
  font-size: 20px;
}

.page-head p,
.muted {
  margin: 0;
  color: #6b7280;
}

.summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 16px;
  padding: 16px;
}

.summary strong {
  color: #d97706;
  font-size: 18px;
}

.danger {
  color: #f56c6c;
}
</style>
