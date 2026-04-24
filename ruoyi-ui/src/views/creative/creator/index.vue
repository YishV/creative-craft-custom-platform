<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>创作者管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于承载商家/手工艺人资料，后续可扩展店铺认证、作品统计、粉丝关系。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增创作者')">新增</el-button>
        <el-button size="mini" @click="tip('编辑创作者')">编辑</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeCreator',
  data() {
    return {
      fields: [
        { name: 'creatorName', desc: '创作者名称' },
        { name: 'storeName', desc: '店铺名称' },
        { name: 'creatorLevel', desc: '创作者等级' },
        { name: 'status', desc: '启用状态' }
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
