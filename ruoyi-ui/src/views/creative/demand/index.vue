<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>定制需求管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于承载买家发起的非标定制需求，是平台区别于普通商城的核心入口之一。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('发布需求')">发布需求</el-button>
        <el-button size="mini" @click="tip('查看进度')">查看进度</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeDemand',
  data() {
    return {
      fields: [
        { name: 'demandTitle', desc: '需求标题' },
        { name: 'categoryId', desc: '需求分类' },
        { name: 'budgetAmount', desc: '预算金额' },
        { name: 'demandStatus', desc: '需求状态' }
      ]
    }
  },
  methods: {
    tip(action) {
      this.$message.info(action + '功能将在下一阶段接入真实 CRUD')
    }
  }
}
</script>

<style scoped>
.actions {
  margin-top: 16px;
}
</style>
