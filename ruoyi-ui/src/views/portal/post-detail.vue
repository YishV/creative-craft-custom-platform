<template>
  <div class="portal-page">
    <el-card v-loading="loading" shadow="never" class="detail-card">
      <div class="hero">{{ post.postTitle | firstChar }}</div>
      <div class="content">
        <div class="meta">
          <el-tag>{{ typeLabel(post.postType) }}</el-tag>
          <span>创作者 #{{ post.creatorId || '-' }}</span>
        </div>
        <h1>{{ post.postTitle || '作品详情' }}</h1>
        <p>{{ post.remark || '暂无作品说明' }}</p>
        <div class="actions">
          <el-button icon="el-icon-user" @click="$router.push('/portal/creators?creatorId=' + post.creatorId)">查看创作者</el-button>
          <el-button type="primary" icon="el-icon-star-off" @click="favoritePost">收藏作品</el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="comment-card">
      <div slot="header">评论</div>
      <el-form :model="commentForm" class="comment-form">
        <el-input v-model="commentForm.commentContent" type="textarea" :rows="3" placeholder="写点评论，别只会潜水。" />
        <el-button type="primary" :loading="submitting" @click="submitComment">发表评论</el-button>
      </el-form>
      <el-empty v-if="!comments.length" description="暂无评论" />
      <div v-for="item in comments" :key="item.commentId" class="comment-item">
        <strong>用户 #{{ item.userId || '-' }}</strong>
        <p>{{ item.commentContent }}</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { addPortalComment, addPortalFavorite, getPortalPost, listPortalComment } from '@/api/creative/portal'

export default {
  name: 'PortalPostDetail',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '作'
    }
  },
  data() {
    return {
      loading: false,
      submitting: false,
      post: {},
      comments: [],
      commentForm: {
        commentContent: ''
      }
    }
  },
  created() {
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      this.loading = true
      getPortalPost(this.$route.params.postId).then(res => {
        this.post = res.data || {}
        this.getComments()
      }).finally(() => {
        this.loading = false
      })
    },
    getComments() {
      listPortalComment({ postId: this.$route.params.postId, pageNum: 1, pageSize: 20 }).then(res => {
        this.comments = res.rows || []
      })
    },
    submitComment() {
      if (!this.commentForm.commentContent) {
        this.$modal.msgWarning('先写评论内容')
        return
      }
      this.submitting = true
      addPortalComment({
        postId: this.post.postId,
        commentContent: this.commentForm.commentContent
      }).then(() => {
        this.commentForm.commentContent = ''
        this.$modal.msgSuccess('评论成功')
        this.getComments()
      }).finally(() => {
        this.submitting = false
      })
    },
    favoritePost() {
      addPortalFavorite({ targetType: 'post', targetId: this.post.postId }).then(() => {
        this.$modal.msgSuccess('已收藏作品')
      })
    },
    typeLabel(type) {
      return { work: '作品分享', process: '制作过程', idea: '灵感记录' }[type] || '作品'
    }
  }
}
</script>

<style scoped>
.portal-page {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background: #f5f7fb;
}

.detail-card,
.comment-card {
  max-width: 920px;
  margin: 0 auto 16px;
  border-radius: 8px;
}

.hero {
  height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 64px;
  background: linear-gradient(135deg, #2d6f73, #b88b4a);
}

.content {
  padding: 20px;
}

.meta,
.actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta span,
.content p,
.comment-item p {
  color: #6b7280;
}

h1 {
  margin: 18px 0 10px;
  font-size: 26px;
}

.content p {
  line-height: 1.8;
}

.comment-form {
  display: grid;
  gap: 12px;
  margin-bottom: 16px;
}

.comment-form .el-button {
  justify-self: end;
}

.comment-item {
  padding: 12px 0;
  border-top: 1px solid #edf0f5;
}
</style>
