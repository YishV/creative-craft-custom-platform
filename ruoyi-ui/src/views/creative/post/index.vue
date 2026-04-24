<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>作品分享管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于社区内容沉淀，后续可扩展作品图片、标签、点赞和首页推荐位。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('发布作品')">发布作品</el-button>
        <el-button size="mini" @click="tip('查看互动')">查看互动</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativePost',
  data() {
    return {
      fields: [
        { name: 'postTitle', desc: '作品标题' },
        { name: 'creatorId', desc: '发布创作者' },
        { name: 'postType', desc: '内容类型' },
        { name: 'status', desc: '发布状态' }
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
