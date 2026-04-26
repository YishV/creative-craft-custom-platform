<template>
  <div class="app-container">
    <el-card v-if="!profile.creator" class="empty-card" shadow="never">
      <div slot="header">创作者主页</div>
      <p>当前账号尚未提交创作者申请。请前往「创作者管理」页提交申请，审核通过后即可入驻并发布商品。</p>
    </el-card>

    <template v-else>
      <el-card class="profile-card" shadow="never">
        <div slot="header" class="card-header">
          <span class="title">{{ profile.creator.creatorName }} · {{ profile.creator.storeName }}</span>
          <el-tag v-if="profile.creator.auditStatus === 'pending'" type="warning">待审核</el-tag>
          <el-tag v-else-if="profile.creator.auditStatus === 'rejected'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="profile.creator.auditStatus === 'approved' && profile.creator.status === '0'" type="success">已通过</el-tag>
          <el-tag v-else type="info">已停用</el-tag>
        </div>
        <el-descriptions :column="2" size="medium" border>
          <el-descriptions-item label="创作者ID">{{ profile.creator.creatorId }}</el-descriptions-item>
          <el-descriptions-item label="等级">{{ profile.creator.creatorLevel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注" :span="2">{{ profile.creator.auditRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ profile.creator.auditBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ profile.creator.auditTime || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-row v-if="effective" :gutter="16" class="stat-row">
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">商品总数</div><div class="stat-value">{{ profile.productCount }}</div></el-card></el-col>
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">在售商品</div><div class="stat-value">{{ profile.onShelfProductCount }}</div></el-card></el-col>
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">待确认报价</div><div class="stat-value">{{ profile.pendingQuoteCount }}</div></el-card></el-col>
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">进行中订单</div><div class="stat-value">{{ profile.activeOrderCount }}</div></el-card></el-col>
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">已完成订单</div><div class="stat-value">{{ profile.completedOrderCount }}</div></el-card></el-col>
        <el-col :span="4"><el-card class="stat-card" shadow="never"><div class="stat-label">累计成交额</div><div class="stat-value">¥{{ profile.completedRevenue || 0 }}</div></el-card></el-col>
      </el-row>

      <el-alert v-if="!effective" class="hint" type="info" show-icon :closable="false"
        :title="hintText" />
    </template>
  </div>
</template>

<script>
import { getMyCreatorProfile } from '@/api/creative/creator'

export default {
  name: 'CreativeMe',
  data() {
    return {
      profile: { creator: null, productCount: 0, onShelfProductCount: 0, pendingQuoteCount: 0, activeOrderCount: 0, completedOrderCount: 0, completedRevenue: 0 }
    }
  },
  computed: {
    effective() {
      return this.profile.creator
        && this.profile.creator.auditStatus === 'approved'
        && this.profile.creator.status === '0'
    },
    hintText() {
      const c = this.profile.creator
      if (!c) return ''
      if (c.auditStatus === 'pending') return '档案审核中，统计数据待审核通过后展示。'
      if (c.auditStatus === 'rejected') return '档案已被驳回，请根据备注修改后重新提交。'
      return '档案已停用，请联系平台管理员恢复。'
    }
  },
  created() {
    this.loadProfile()
  },
  methods: {
    loadProfile() {
      getMyCreatorProfile().then(res => {
        if (res && res.data) {
          this.profile = res.data
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-card { margin-bottom: 16px; }
.card-header { display: flex; align-items: center; gap: 12px; }
.card-header .title { font-weight: 600; font-size: 16px; }
.stat-row { margin-top: 8px; }
.stat-card { text-align: center; }
.stat-label { color: #888; font-size: 13px; }
.stat-value { font-size: 22px; font-weight: 600; margin-top: 4px; }
.hint { margin-top: 16px; }
.empty-card p { color: #666; line-height: 1.8; }
</style>
