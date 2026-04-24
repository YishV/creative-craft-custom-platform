<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>手作商品管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于维护可售卖的手作商品，为购物车、订单和内容推荐预留数据入口。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增商品')">新增</el-button>
        <el-button size="mini" @click="tip('预览商品详情')">预览</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeProduct',
  data() {
    return {
      fields: [
        { name: 'productName', desc: '商品名称' },
        { name: 'creatorId', desc: '所属创作者' },
        { name: 'categoryId', desc: '所属分类' },
        { name: 'price', desc: '商品价格' }
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
