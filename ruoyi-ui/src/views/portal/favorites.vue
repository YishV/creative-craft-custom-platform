<template>
  <div class="portal-page">
    <el-tabs v-model="queryParams.targetType" @tab-click="handleQuery">
      <el-tab-pane label="关注的创作者" name="creator" />
      <el-tab-pane label="收藏的商品" name="product" />
      <el-tab-pane label="收藏的作品" name="post" />
    </el-tabs>

    <div v-if="favorites.length" class="batch-bar">
      <el-checkbox :indeterminate="indeterminate" :value="allSelected" @change="toggleSelectAll">全选</el-checkbox>
      <span class="selected-tip">已选 {{ selectedIds.length }} / {{ favorites.length }}</span>
      <el-button
        size="mini"
        type="danger"
        icon="el-icon-delete"
        :disabled="!selectedIds.length"
        :loading="batchLoading"
        @click="batchCancel"
      >批量取消收藏</el-button>
    </div>

    <el-empty v-if="!loading && !favorites.length" :description="emptyText">
      <el-button type="primary" @click="goEmptyTarget">去看看</el-button>
    </el-empty>

    <div v-else v-loading="loading" class="favorite-grid">
      <article
        v-for="row in favorites"
        :key="row.favoriteId"
        class="favorite-card"
        :class="{ 'is-selected': selectedIds.includes(row.favoriteId) }"
      >
        <el-checkbox
          class="card-checkbox"
          :value="selectedIds.includes(row.favoriteId)"
          @change="toggleSelect(row.favoriteId)"
        />
        <div class="cover" :class="'cover-' + row.targetType">{{ row.targetCover || row.targetName | firstChar }}</div>
        <div class="body">
          <div class="head">
            <el-tag size="mini" :type="tagType(row.targetType)">{{ typeLabel(row.targetType) }}</el-tag>
            <span class="time">{{ row.createTime }}</span>
          </div>
          <h2 :title="row.targetName">{{ row.targetName || ('#' + row.targetId) }}</h2>
          <p class="subtitle">{{ row.targetSubtitle || '暂无简介' }}</p>
          <p v-if="row.remark" class="remark" :title="row.remark">备注：{{ row.remark }}</p>
          <div class="actions">
            <el-button size="mini" type="primary" plain icon="el-icon-view" @click="goTarget(row)">查看详情</el-button>
            <el-button size="mini" type="danger" plain icon="el-icon-close" @click="cancel(row)">取消收藏</el-button>
          </div>
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
import { cancelPortalFavorite, listPortalFavorite } from '@/api/creative/portal'

export default {
  name: 'PortalFavorites',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '收'
    }
  },
  data() {
    return {
      loading: false,
      favorites: [],
      total: 0,
      selectedIds: [],
      batchLoading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        targetType: 'creator'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      this.selectedIds = []
      listPortalFavorite(this.queryParams).then(res => {
        this.favorites = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    cancel(row) {
      cancelPortalFavorite(row.favoriteId).then(() => {
        this.$modal.msgSuccess('已取消收藏')
        this.getList()
      })
    },
    toggleSelect(favoriteId) {
      const idx = this.selectedIds.indexOf(favoriteId)
      if (idx >= 0) {
        this.selectedIds.splice(idx, 1)
      } else {
        this.selectedIds.push(favoriteId)
      }
    },
    toggleSelectAll(checked) {
      this.selectedIds = checked ? this.favorites.map(item => item.favoriteId) : []
    },
    batchCancel() {
      if (!this.selectedIds.length) {
        return
      }
      this.$modal.confirm(`确认取消选中的 ${this.selectedIds.length} 条收藏？`).then(() => {
        this.batchLoading = true
        const tasks = this.selectedIds.map(id => cancelPortalFavorite(id))
        Promise.all(tasks).then(() => {
          this.$modal.msgSuccess('已批量取消收藏')
          this.getList()
        }).finally(() => {
          this.batchLoading = false
        })
      }).catch(() => {})
    },
    goTarget(row) {
      if (row.targetType === 'post') {
        this.$router.push('/portal/post/' + row.targetId)
      } else if (row.targetType === 'product') {
        this.$router.push('/portal/products?productId=' + row.targetId)
      } else {
        this.$router.push('/portal/creators?creatorId=' + row.targetId)
      }
    },
    typeLabel(type) {
      return { creator: '创作者', product: '商品', post: '作品' }[type] || type
    },
    tagType(type) {
      return { creator: 'success', product: 'warning', post: '' }[type] || ''
    },
    goEmptyTarget() {
      if (this.queryParams.targetType === 'product') {
        this.$router.push('/portal/products')
        return
      }
      if (this.queryParams.targetType === 'post') {
        this.$router.push('/portal/community')
        return
      }
      this.$router.push('/portal/creators')
    }
  },
  computed: {
    emptyText() {
      return {
        creator: '暂无关注的创作者',
        product: '暂无收藏的商品',
        post: '暂无收藏的作品'
      }[this.queryParams.targetType] || '暂无收藏内容'
    },
    allSelected() {
      return this.favorites.length > 0 && this.selectedIds.length === this.favorites.length
    },
    indeterminate() {
      return this.selectedIds.length > 0 && this.selectedIds.length < this.favorites.length
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

.batch-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 14px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.selected-tip {
  flex: 1;
  color: #6b7280;
  font-size: 13px;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 0;
}

.favorite-card {
  position: relative;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
  display: flex;
  flex-direction: column;
  transition: transform .15s ease, box-shadow .15s ease, outline-color .15s ease;
  outline: 2px solid transparent;
}

.favorite-card.is-selected {
  outline-color: #d97706;
}

.favorite-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(15, 35, 52, .1);
}

.card-checkbox {
  position: absolute;
  top: 8px;
  left: 10px;
  z-index: 2;
  background: rgba(255, 255, 255, .85);
  border-radius: 4px;
  padding: 2px 4px;
}

.cover {
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 42px;
  font-weight: 600;
}

.cover-product {
  background: linear-gradient(135deg, #297e7b, #6b9f88);
}

.cover-creator {
  background: linear-gradient(135deg, #2d6f73, #b88b4a);
}

.cover-post {
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
}

.body {
  padding: 14px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.time {
  color: #9ca3af;
  font-size: 12px;
}

h2 {
  margin: 2px 0 0;
  font-size: 16px;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.subtitle {
  margin: 0;
  color: #4b5563;
  font-size: 13px;
}

.remark {
  margin: 0;
  color: #9ca3af;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.actions {
  margin-top: auto;
  display: flex;
  gap: 8px;
  padding-top: 6px;
}
</style>
