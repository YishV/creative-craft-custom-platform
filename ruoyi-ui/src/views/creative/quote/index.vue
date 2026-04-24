<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>定制报价管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于实现创作者对定制需求的响应报价，后续可扩展议价、沟通与选中逻辑。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增报价')">新增报价</el-button>
        <el-button size="mini" @click="tip('选择报价')">选择报价</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeQuote',
  data() {
    return {
      fields: [
        { name: 'demandId', desc: '关联需求' },
        { name: 'creatorId', desc: '报价创作者' },
        { name: 'quoteAmount', desc: '报价金额' },
        { name: 'deliveryDays', desc: '交付天数' }
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
