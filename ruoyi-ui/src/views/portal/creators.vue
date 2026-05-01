<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="创作者">
        <el-input v-model="queryParams.creatorName" placeholder="搜索创作者" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!loading && !creators.length" description="暂无创作者" />
    <div v-loading="loading" class="creator-grid">
      <article v-for="item in creators" :key="item.creatorId" class="creator-card">
        <div class="avatar">{{ item.creatorName | firstChar }}</div>
        <h2>{{ item.creatorName }}</h2>
        <p>{{ item.storeName || '个人手作工作室' }}</p>
        <div class="meta">
          <el-tag size="mini">{{ levelLabel(item.creatorLevel) }}</el-tag>
          <span>#{{ item.creatorId }}</span>
        </div>
        <div class="actions">
          <el-button size="mini" icon="el-icon-goods" @click="$router.push('/portal/products?creatorId=' + item.creatorId)">查看商品</el-button>
          <el-button 
            :type="followedMap[item.creatorId] ? 'success' : 'primary'" 
            plain size="mini" 
            :icon="followedMap[item.creatorId] ? 'el-icon-check' : 'el-icon-star-off'" 
            @click="followCreator(item)">
            {{ followedMap[item.creatorId] ? '已关注' : '关注' }}
          </el-button>
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
import { listPortalCreator } from '@/api/creative/portal'
import { toggleFollow, checkInteractionStatus } from '@/api/creative/interaction'

export default {
  name: 'PortalCreators',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '创'
    }
  },
  data() {
    return {
      loading: false,
      creators: [],
      total: 0,
      followedMap: {}, // 关注状态映射
      queryParams: {
        pageNum: 1,
        pageSize: 12,
        creatorName: undefined
      }
    }
  },
  created() {
    if (this.$route.query.creatorId) {
      this.queryParams.creatorId = this.$route.query.creatorId
    }
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPortalCreator(this.queryParams).then(res => {
        this.creators = res.rows || []
        this.total = res.total || 0
        // 批量检查关注状态
        this.creators.forEach(item => {
          checkInteractionStatus('creator', item.creatorId, item.creatorId).then(resStatus => {
            this.$set(this.followedMap, item.creatorId, resStatus.followed)
          })
        })
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.creatorName = undefined
      this.queryParams.creatorId = undefined
      this.handleQuery()
    },
    followCreator(item) {
      toggleFollow(item.creatorId).then(() => {
        const newState = !this.followedMap[item.creatorId]
        this.$set(this.followedMap, item.creatorId, newState)
        this.$modal.msgSuccess(newState ? '关注成功' : '已取消关注')
      })
    },
    levelLabel(level) {
      return { normal: '普通创作者', premium: '优选创作者', master: '匠心创作者' }[level] || '创作者'
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
.creator-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.toolbar {
  padding: 16px 16px 0;
}

.creator-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.creator-card {
  padding: 18px;
  text-align: center;
}

.avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  border-radius: 50%;
  background: #2d6f73;
  font-size: 28px;
}

h2 {
  margin: 0 0 8px;
  font-size: 18px;
}

p,
.meta span {
  color: #6b7280;
}

.meta,
.actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.actions {
  margin-top: 14px;
}
</style>
