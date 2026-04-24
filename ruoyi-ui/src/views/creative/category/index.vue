<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>文创分类管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于统一管理文创分类树，为商品发布、搜索筛选和推荐维度提供基础数据。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增分类')">新增</el-button>
        <el-button size="mini" @click="tip('维护分类树')">分类树</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeCategory',
  data() {
    return {
      fields: [
        { name: 'parentId', desc: '父级分类' },
        { name: 'categoryName', desc: '分类名称' },
        { name: 'categoryCode', desc: '分类编码' },
        { name: 'sortNum', desc: '排序值' }
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
