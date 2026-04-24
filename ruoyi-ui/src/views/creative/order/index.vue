<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>定制订单管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于承载订单生成、状态跟踪、发货确认等流程，后续可挂接支付和售后逻辑。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增订单')">新增订单</el-button>
        <el-button size="mini" @click="tip('推进订单状态')">状态流转</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeOrder',
  data() {
    return {
      fields: [
        { name: 'orderNo', desc: '订单编号' },
        { name: 'buyerId', desc: '买家编号' },
        { name: 'sellerId', desc: '卖家编号' },
        { name: 'orderStatus', desc: '订单状态' }
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
