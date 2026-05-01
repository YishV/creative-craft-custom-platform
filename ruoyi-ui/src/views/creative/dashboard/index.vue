<template>
  <div class="app-container creative-dashboard" v-loading="loading">
    <el-row :gutter="20" class="card-row">
      <el-col :xs="12" :sm="8" :md="4" v-for="card in cards" :key="card.key">
        <div :class="['stat-card', card.theme]">
          <div class="stat-card-label">{{ card.label }}</div>
          <div class="stat-card-value">{{ card.value }}</div>
          <div v-if="card.hint" class="stat-card-hint">{{ card.hint }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <div slot="header" class="chart-header">
            <span>近 7 日订单趋势</span>
            <span class="chart-sub">订单数 / 已支付 GMV</span>
          </div>
          <div ref="trendChart" class="chart-canvas"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <div slot="header" class="chart-header">
            <span>热门分类 Top 5</span>
            <span class="chart-sub">按商品订单数</span>
          </div>
          <div ref="categoryChart" class="chart-canvas"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="never">
          <div slot="header" class="chart-header">
            <span>活跃创作者 Top 5</span>
            <span class="chart-sub">近 30 日发布商品数</span>
          </div>
          <div v-if="activeCreators.length === 0" class="empty-tip">暂无近 30 日活跃创作者</div>
          <el-table v-else :data="activeCreators" stripe>
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="creatorName" label="创作者" />
            <el-table-column prop="productCount" label="近 30 日发布商品数" align="right" width="220" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/creative/dashboard'

export default {
  name: 'CreativeDashboard',
  data() {
    return {
      loading: false,
      summary: {
        productCount: 0,
        productOnSaleCount: 0,
        orderCount: 0,
        paidOrderCount: 0,
        gmv: 0,
        activeCreatorCount: 0
      },
      trend: [],
      hotCategories: [],
      activeCreators: [],
      trendChart: null,
      categoryChart: null,
      resizeHandler: null
    }
  },
  computed: {
    cards() {
      return [
        { key: 'product', label: '商品总数', value: this.summary.productCount, theme: 'theme-blue', hint: `已上架 ${this.summary.productOnSaleCount}` },
        { key: 'orderTotal', label: '订单总数', value: this.summary.orderCount, theme: 'theme-purple', hint: `已支付 ${this.summary.paidOrderCount}` },
        { key: 'gmv', label: '已支付 GMV', value: this.formatMoney(this.summary.gmv), theme: 'theme-green' },
        { key: 'activeCreator', label: '活跃创作者', value: this.summary.activeCreatorCount, theme: 'theme-orange', hint: '近 30 日有发布' },
        { key: 'hotCategory', label: '热门分类数', value: this.hotCategories.length, theme: 'theme-cyan' },
        { key: 'paidRate', label: '订单支付率', value: this.computePaidRate(), theme: 'theme-pink' }
      ]
    }
  },
  mounted() {
    this.loadStats()
    this.resizeHandler = () => {
      this.trendChart && this.trendChart.resize()
      this.categoryChart && this.categoryChart.resize()
    }
    window.addEventListener('resize', this.resizeHandler)
  },
  beforeDestroy() {
    if (this.resizeHandler) {
      window.removeEventListener('resize', this.resizeHandler)
    }
    this.trendChart && this.trendChart.dispose()
    this.categoryChart && this.categoryChart.dispose()
  },
  methods: {
    loadStats() {
      this.loading = true
      getDashboardStats()
        .then(res => {
          const data = res.data || {}
          this.summary = Object.assign(this.summary, data.summary || {})
          this.trend = data.trend || []
          this.hotCategories = data.hotCategories || []
          this.activeCreators = data.activeCreators || []
          this.$nextTick(() => {
            this.renderTrendChart()
            this.renderCategoryChart()
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    renderTrendChart() {
      const dom = this.$refs.trendChart
      if (!dom) return
      if (!this.trendChart) {
        this.trendChart = echarts.init(dom)
      }
      this.trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['订单数', '已支付 GMV'] },
        grid: { left: 50, right: 50, bottom: 40, top: 40 },
        xAxis: { type: 'category', data: this.trend.map(t => t.date) },
        yAxis: [
          { type: 'value', name: '订单数' },
          { type: 'value', name: 'GMV (元)' }
        ],
        series: [
          {
            name: '订单数',
            type: 'bar',
            data: this.trend.map(t => t.orderCount),
            itemStyle: { color: '#409eff' }
          },
          {
            name: '已支付 GMV',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            data: this.trend.map(t => Number(t.gmv || 0)),
            itemStyle: { color: '#67c23a' }
          }
        ]
      })
    },
    renderCategoryChart() {
      const dom = this.$refs.categoryChart
      if (!dom) return
      if (!this.categoryChart) {
        this.categoryChart = echarts.init(dom)
      }
      if (this.hotCategories.length === 0) {
        this.categoryChart.setOption({
          title: { text: '暂无商品订单数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } },
          series: []
        }, true)
        return
      }
      this.categoryChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 单 ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [
          {
            type: 'pie',
            radius: ['45%', '70%'],
            center: ['60%', '50%'],
            label: { formatter: '{b}\n{c} 单' },
            data: this.hotCategories.map(c => ({ name: c.categoryName, value: c.orderCount }))
          }
        ]
      }, true)
    },
    formatMoney(val) {
      const n = Number(val || 0)
      return '￥' + n.toFixed(2)
    },
    computePaidRate() {
      const total = Number(this.summary.orderCount || 0)
      if (total === 0) return '0%'
      const paid = Number(this.summary.paidOrderCount || 0)
      return ((paid / total) * 100).toFixed(1) + '%'
    }
  }
}
</script>

<style scoped>
.creative-dashboard {
  padding: 20px;
}
.card-row {
  margin-bottom: 16px;
}
.stat-card {
  border-radius: 6px;
  padding: 18px 20px;
  color: #fff;
  margin-bottom: 16px;
  min-height: 96px;
}
.stat-card-label {
  font-size: 14px;
  opacity: 0.85;
}
.stat-card-value {
  font-size: 26px;
  font-weight: 600;
  margin-top: 8px;
}
.stat-card-hint {
  font-size: 12px;
  margin-top: 6px;
  opacity: 0.85;
}
.theme-blue { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.theme-purple { background: linear-gradient(135deg, #a18cd1, #fbc2eb); }
.theme-green { background: linear-gradient(135deg, #43e97b, #38f9d7); }
.theme-orange { background: linear-gradient(135deg, #fa709a, #fee140); }
.theme-cyan { background: linear-gradient(135deg, #30cfd0, #330867); }
.theme-pink { background: linear-gradient(135deg, #ff9a9e, #fad0c4); }
.chart-row {
  margin-bottom: 16px;
}
.chart-canvas {
  width: 100%;
  height: 320px;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.chart-sub {
  font-size: 12px;
  color: #999;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
</style>
