<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="作品">
        <el-input v-model="queryParams.postTitle" placeholder="搜索作品标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="queryParams.postType" placeholder="全部类型" clearable>
          <el-option label="作品分享" value="work" />
          <el-option label="制作过程" value="process" />
          <el-option label="灵感记录" value="idea" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!loading && !posts.length" description="暂无社区作品" />
    <div v-loading="loading" class="post-grid">
      <article v-for="item in posts" :key="item.postId" class="post-card" @click="$router.push('/portal/post/' + item.postId)">
        <div class="cover">{{ item.postTitle | firstChar }}</div>
        <div class="body">
          <div class="meta">
            <el-tag size="mini">{{ typeLabel(item.postType) }}</el-tag>
            <span>创作者 #{{ item.creatorId || '-' }}</span>
          </div>
          <h2>{{ item.postTitle }}</h2>
          <p>{{ item.remark || '这位创作者很懒，作品说明还没写。' }}</p>
          <el-button type="text">查看详情</el-button>
        </div>
      </article>
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listPortalPost } from '@/api/creative/portal'

export default {
  name: 'PortalCommunity',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '作'
    }
  },
  data() {
    return {
      loading: false,
      posts: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 9,
        postTitle: undefined,
        postType: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPortalPost(this.queryParams).then(res => {
        this.posts = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.postTitle = undefined
      this.queryParams.postType = undefined
      this.handleQuery()
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

.toolbar,
.post-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.toolbar {
  padding: 16px 16px 0;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.post-card {
  overflow: hidden;
  cursor: pointer;
}

.cover {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 42px;
  background: linear-gradient(135deg, #2d6f73, #b88b4a);
}

.body {
  padding: 14px;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta span,
p {
  color: #6b7280;
}

h2 {
  margin: 12px 0 8px;
  font-size: 17px;
}

p {
  min-height: 44px;
  margin: 0;
  line-height: 1.6;
}
</style>
