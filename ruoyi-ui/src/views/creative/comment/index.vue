<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>评论互动管理</span>
        <el-tag size="mini" type="success" style="float: right">基础骨架</el-tag>
      </div>
      <el-alert title="用于管理作品评论和互动记录，后续可挂接敏感词过滤、审核与举报处理。" type="info" :closable="false" />
      <el-divider content-position="left">预留字段</el-divider>
      <el-table :data="fields" border>
        <el-table-column prop="name" label="字段" />
        <el-table-column prop="desc" label="说明" />
      </el-table>
      <div class="actions">
        <el-button type="primary" size="mini" @click="tip('新增评论')">新增评论</el-button>
        <el-button size="mini" @click="tip('审核评论')">审核</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'CreativeComment',
  data() {
    return {
      fields: [
        { name: 'postId', desc: '所属作品' },
        { name: 'userId', desc: '评论用户' },
        { name: 'commentContent', desc: '评论内容' },
        { name: 'auditStatus', desc: '审核状态' }
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
