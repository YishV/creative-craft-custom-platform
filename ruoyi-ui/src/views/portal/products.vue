<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="商品">
        <el-input v-model="queryParams.productName" placeholder="搜索商品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="queryParams.productType" placeholder="全部类型" clearable>
          <el-option label="现货" value="spot" />
          <el-option label="定制" value="custom" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!loading && !products.length" description="暂无上架商品" />
    <div v-loading="loading" class="product-grid">
      <article v-for="item in products" :key="item.productId" class="product-card">
        <div class="cover">{{ item.productName | firstChar }}</div>
        <div class="body">
          <div class="meta">
            <el-tag size="mini" :type="item.productType === 'custom' ? 'warning' : 'success'">{{ item.productType === 'custom' ? '可定制' : '现货' }}</el-tag>
            <span>创作者 #{{ item.creatorId || '-' }}</span>
          </div>
          <h2>{{ item.productName }}</h2>
          <p>{{ item.remark || '这件作品暂时没有详细介绍' }}</p>
          <div class="foot">
            <strong>￥{{ money(item.price) }}</strong>
            <el-button type="primary" plain size="mini" @click="showDetail(item)">查看详情</el-button>
          </div>
        </div>
      </article>
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="detail.productName || '商品详情'" :visible.sync="detailOpen" width="520px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品类型">{{ detail.productType === 'custom' ? '可定制' : '现货' }}</el-descriptions-item>
        <el-descriptions-item label="价格">￥{{ money(detail.price) }}</el-descriptions-item>
        <el-descriptions-item label="创作者">#{{ detail.creatorId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="说明">{{ detail.remark || '暂无说明' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listPortalProduct, getPortalProduct } from '@/api/creative/portal'

export default {
  name: 'PortalProducts',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '作'
    }
  },
  data() {
    return {
      loading: false,
      products: [],
      total: 0,
      detailOpen: false,
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 8,
        productName: undefined,
        productType: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPortalProduct(this.queryParams).then(res => {
        this.products = res.rows || []
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
      this.queryParams.productName = undefined
      this.queryParams.productType = undefined
      this.handleQuery()
    },
    showDetail(row) {
      getPortalProduct(row.productId).then(res => {
        this.detail = res.data || row
        this.detailOpen = true
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

.toolbar {
  padding: 16px 16px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.product-card {
  min-height: 240px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.cover {
  height: 108px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 42px;
  background: linear-gradient(135deg, #297e7b, #6b9f88);
}

.body {
  padding: 14px;
}

.meta,
.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta span {
  color: #6b7280;
  font-size: 12px;
}

h2 {
  margin: 12px 0 8px;
  color: #1f2937;
  font-size: 17px;
}

p {
  min-height: 44px;
  margin: 0 0 14px;
  color: #6b7280;
  line-height: 1.6;
}

.foot strong {
  color: #d97706;
  font-size: 18px;
}
</style>
