<template>
  <div class="portal-page">
    <el-tabs v-model="queryParams.targetType" @tab-click="handleQuery">
      <el-tab-pane label="关注的创作者" name="creator" />
      <el-tab-pane label="收藏的商品" name="product" />
      <el-tab-pane label="收藏的作品" name="post" />
    </el-tabs>

    <el-empty v-if="!loading && !favorites.length" :description="emptyText">
      <el-button type="primary" @click="goEmptyTarget">去看看</el-button>
    </el-empty>

    <el-table v-else v-loading="loading" :data="favorites" class="portal-table">
      <el-table-column label="类型" width="130">
        <template slot-scope="scope">
          <el-tag size="mini">{{ typeLabel(scope.row.targetType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标ID" prop="targetId" width="120" />
      <el-table-column label="备注" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="收藏时间" prop="createTime" width="170" />
      <el-table-column label="操作" width="180" align="center">
        <template slot-scope="scope">
          <el-button type="text" @click="goTarget(scope.row)">查看</el-button>
          <el-button type="text" class="danger" @click="cancel(scope.row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

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
  data() {
    return {
      loading: false,
      favorites: [],
      total: 0,
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
    goTarget(row) {
      if (row.targetType === 'post') {
        this.$router.push('/portal/post/' + row.targetId)
      } else if (row.targetType === 'product') {
        this.$router.push('/portal/products')
      } else {
        this.$router.push('/portal/creators?creatorId=' + row.targetId)
      }
    },
    typeLabel(type) {
      return { creator: '创作者', product: '商品', post: '作品' }[type] || type
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

.portal-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.danger {
  color: #f56c6c;
}
</style>
