<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>收藏关注管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于维护用户与目标对象之间的关注/收藏关系，后续可用于个性化推荐与用户画像。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增关系')">新增关系</el-button>
        <el-button size="mini" @click="tip('查看画像')">用户画像</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeFavorite',
  data() {
    return {
      fields: [
        { name: 'userId', desc: '用户编号' },
        { name: 'targetType', desc: '目标类型' },
        { name: 'targetId', desc: '目标编号' },
        { name: 'status', desc: '关系状态' }
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
