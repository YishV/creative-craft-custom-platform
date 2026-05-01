<template>
  <div class="portal-page">
    <section class="portal-hero">
      <div>
        <p class="eyebrow">文创手作定制交易平台</p>
        <h1>从作品浏览到定制跟进，一条线演示清楚</h1>
        <p class="hero-copy">这里是买家和创作者共用的前台入口，可浏览上架商品、查看开放需求、跟踪订单进度，并进入社区、聊天和收藏页面。</p>
        <div class="hero-actions">
          <el-button type="primary" icon="el-icon-goods" @click="$router.push('/portal/products')">浏览商品</el-button>
          <el-button icon="el-icon-edit-outline" @click="$router.push('/portal/demands')">查看需求</el-button>
          <el-button icon="el-icon-shopping-cart-2" @click="$router.push('/portal/cart')">购物车</el-button>
        </div>
      </div>
    </section>

    <el-row :gutter="16" class="quick-row">
      <el-col :xs="12" :sm="6" v-for="item in quickLinks" :key="item.title">
        <button class="quick-card" type="button" @click="$router.push(item.path)">
          <i :class="item.icon"></i>
          <span>{{ item.title }}</span>
          <small>{{ item.desc }}</small>
        </button>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="item in stats" :key="item.label">
        <div class="stat-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <section class="portal-section">
          <div class="section-head">
            <h2>最新上架</h2>
            <el-button type="text" @click="$router.push('/portal/products')">更多</el-button>
          </div>
          <el-empty v-if="!products.length" description="暂无上架商品" />
          <div v-else class="product-grid">
            <div v-for="product in products" :key="product.productId" class="product-card">
              <div class="product-cover">{{ product.productName | firstChar }}</div>
              <div class="product-body">
                <h3>{{ product.productName }}</h3>
                <p>{{ product.remark || '创作者暂未填写商品介绍' }}</p>
                <div class="price">￥{{ money(product.price) }}</div>
              </div>
            </div>
          </div>
        </section>
      </el-col>
      <el-col :xs="24" :lg="10">
        <section class="portal-section">
          <div class="section-head">
            <h2>开放需求</h2>
            <el-button type="text" @click="$router.push('/portal/demands')">去报价</el-button>
          </div>
          <el-empty v-if="!demands.length" description="暂无开放需求" />
          <div v-else class="demand-list">
            <div v-for="demand in demands" :key="demand.demandId" class="demand-item">
              <div>
                <h3>{{ demand.demandTitle }}</h3>
                <p>{{ demand.remark || '买家暂未补充说明' }}</p>
              </div>
              <strong>￥{{ money(demand.budgetAmount) }}</strong>
            </div>
          </div>
        </section>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listPortalProduct, listPortalDemand } from '@/api/creative/portal'
import { listOrder } from '@/api/creative/order'

export default {
  name: 'PortalIndex',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '作'
    }
  },
  data() {
    return {
      products: [],
      demands: [],
      orderTotal: 0,
      productTotal: 0,
      demandTotal: 0,
      quickLinks: [
        { title: '购物车', desc: '结算与支付', path: '/portal/cart', icon: 'el-icon-shopping-cart-2' },
        { title: '在线沟通', desc: '实时联系创作者', path: '/portal/chat', icon: 'el-icon-chat-dot-round' },
        { title: '创作者', desc: '浏览创作者主页', path: '/portal/creators', icon: 'el-icon-user' },
        { title: '收藏关注', desc: '查看我的收藏', path: '/portal/favorites', icon: 'el-icon-star-off' }
      ]
    }
  },
  computed: {
    stats() {
      return [
        { label: '上架商品', value: this.productTotal },
        { label: '开放需求', value: this.demandTotal },
        { label: '我的订单', value: this.orderTotal },
        { label: '当前账号', value: this.$store.getters.nickName || this.$store.getters.name || '-' }
      ]
    }
  },
  created() {
    this.loadDashboard()
  },
  methods: {
    loadDashboard() {
      listPortalProduct({ pageNum: 1, pageSize: 4 }).then(res => {
        this.products = res.rows || []
        this.productTotal = res.total || 0
      })
      listPortalDemand({ pageNum: 1, pageSize: 5 }).then(res => {
        this.demands = res.rows || []
        this.demandTotal = res.total || 0
      })
      listOrder({ pageNum: 1, pageSize: 1 }).then(res => {
        this.orderTotal = res.total || 0
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

.portal-hero {
  min-height: 260px;
  padding: 36px;
  color: #fff;
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(24, 81, 110, .96), rgba(41, 126, 123, .9)), url('../../assets/images/login-background.jpg') center/cover;
  border-radius: 8px;
}

.eyebrow {
  margin: 0 0 12px;
  font-size: 13px;
  opacity: .86;
}

.portal-hero h1 {
  max-width: 720px;
  margin: 0;
  font-size: 34px;
  line-height: 1.28;
  font-weight: 700;
}

.hero-copy {
  max-width: 620px;
  margin: 14px 0 22px;
  font-size: 15px;
  line-height: 1.8;
  opacity: .92;
}

.hero-actions .el-button + .el-button {
  margin-left: 10px;
}

.quick-row,
.stat-row {
  margin-top: 16px;
}

.quick-card,
.stat-card,
.portal-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.quick-card {
  width: 100%;
  height: 96px;
  border: 0;
  text-align: left;
  padding: 16px;
  cursor: pointer;
}

.quick-card i {
  color: #297e7b;
  font-size: 24px;
}

.quick-card span,
.quick-card small {
  display: block;
}

.quick-card span {
  margin-top: 8px;
  color: #1f2937;
  font-weight: 700;
}

.quick-card small {
  margin-top: 4px;
  color: #6b7280;
}

.stat-card {
  height: 96px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.stat-card span {
  color: #6b7280;
}

.stat-card strong {
  color: #1f2937;
  font-size: 24px;
}

.portal-section {
  margin-top: 16px;
  padding: 18px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.section-head h2 {
  margin: 0;
  font-size: 18px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.product-card {
  display: flex;
  min-height: 132px;
  border: 1px solid #edf0f5;
  border-radius: 8px;
  overflow: hidden;
}

.product-cover {
  width: 104px;
  flex: 0 0 104px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 38px;
  color: #fff;
  background: #297e7b;
}

.product-body {
  padding: 12px;
  min-width: 0;
}

.product-body h3,
.demand-item h3 {
  margin: 0 0 8px;
  font-size: 15px;
  color: #1f2937;
}

.product-body p,
.demand-item p {
  margin: 0;
  color: #6b7280;
  line-height: 1.6;
}

.price,
.demand-item strong {
  margin-top: 10px;
  color: #d97706;
  font-weight: 700;
}

.demand-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #edf0f5;
}

.demand-item:last-child {
  border-bottom: 0;
}

@media (max-width: 768px) {
  .portal-hero {
    padding: 24px;
  }

  .portal-hero h1 {
    font-size: 26px;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }
}
</style>
