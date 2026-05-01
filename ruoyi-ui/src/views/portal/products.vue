<template>
  <div class="portal-page">
    <el-form :model="queryParams" size="small" inline class="toolbar">
      <el-form-item label="商品">
        <el-input v-model="queryParams.productName" placeholder="搜索商品名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="queryParams.productType" placeholder="全部类型" clearable>
          <el-option label="现货" value="spot" />
          <el-option label="定制" value="custom" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-empty v-if="!loading && !products.length" description="暂无上架商品" />
    <div v-loading="loading" class="product-grid">
      <article v-for="item in products" :key="item.productId" class="product-card">
        <div class="cover">{{ item.productName | firstChar }}</div>
        <div class="body">
          <div class="meta">
            <el-tag size="mini" :type="item.productType === 'custom' ? 'warning' : 'success'">{{ item.productType === 'custom' ? '可定制' : '现货' }}</el-tag>
            <span>创作者 #{{ item.creatorId || '-' }}</span>
          </div>
          <h2>{{ item.productName }}</h2>
          <p>{{ item.remark || '这件商品暂未填写详细介绍' }}</p>
          <div class="foot">
            <strong>￥{{ money(item.price) }}</strong>
            <div>
              <el-button size="mini" icon="el-icon-star-off" @click="favoriteProduct(item)">收藏</el-button>
              <el-button size="mini" icon="el-icon-shopping-cart-2" @click="addToCart(item)">加入购物车</el-button>
              <el-button type="primary" plain size="mini" @click="showDetail(item)">查看详情</el-button>
            </div>
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

    <el-dialog :title="detail.productName || '商品详情'" :visible.sync="detailOpen" width="520px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品类型">{{ detail.productType === 'custom' ? '可定制' : '现货' }}</el-descriptions-item>
        <el-descriptions-item label="价格">￥{{ money(detail.price) }}</el-descriptions-item>
        <el-descriptions-item label="创作者">#{{ detail.creatorId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="说明">{{ detail.remark || '暂无商品说明' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 交互区 -->
      <div class="interaction-bar">
        <el-button :type="isLiked ? 'danger' : 'default'" icon="el-icon-caret-top" size="mini" @click="handleLike">
          {{ isLiked ? '已点赞' : '点赞' }}
        </el-button>
        <el-button icon="el-icon-share" size="mini" @click="handleShare">分享</el-button>
      </div>

      <!-- 评论区 -->
      <div class="comment-section">
        <h3>用户评价 ({{ commentTotal }})</h3>
        <div class="comment-input">
          <el-input v-model="newComment" placeholder="写下你的评价" size="small">
            <el-button slot="append" @click="submitComment">发表</el-button>
          </el-input>
        </div>
        <div class="comment-list">
          <div v-for="c in comments" :key="c.commentId" class="comment-item">
            <div class="comment-user"><strong>{{ c.userName }}</strong> <span>{{ c.gmtCreate }}</span></div>
            <div class="comment-content">{{ c.content }}</div>
          </div>
          <div v-if="!comments.length" class="no-comment">暂无评价，欢迎发表第一条评价</div>
        </div>
      </div>

      <div slot="footer">
        <el-button @click="detailOpen = false">关闭</el-button>
        <el-button icon="el-icon-chat-dot-round" type="success" @click="handleContact(detail)">联系创作者</el-button>
        <el-button icon="el-icon-star-off" @click="favoriteProduct(detail)">收藏商品</el-button>
        <el-button type="primary" icon="el-icon-shopping-cart-2" @click="addToCart(detail)">加入购物车</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { addPortalFavorite, listPortalProduct, getPortalProduct } from '@/api/creative/portal'
import { openSession } from '@/api/creative/chat'
import { addCartItem } from '@/utils/portalCart'
import { listComments, addComment, toggleLike, checkInteractionStatus, addShare } from '@/api/creative/interaction'

export default {
  name: 'PortalProducts',
  filters: {
    firstChar(value) {
      return value ? String(value).slice(0, 1) : '作'
    }
  },
  data() {
    return {
      loading: false,
      products: [],
      total: 0,
      detailOpen: false,
      detail: {},
      // 交互相关
      comments: [],
      commentTotal: 0,
      newComment: '',
      isLiked: false,
      queryParams: {
        pageNum: 1,
        pageSize: 8,
        productName: undefined,
        productType: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPortalProduct(this.queryParams).then(res => {
        this.products = res.rows || []
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
      this.queryParams.productName = undefined
      this.queryParams.productType = undefined
      this.handleQuery()
    },
    showDetail(row) {
      getPortalProduct(row.productId).then(res => {
        this.detail = res.data || row
        this.detailOpen = true
        this.loadInteractionData()
      })
    },
    loadInteractionData() {
      listComments({ targetType: 'product', targetId: this.detail.productId, pageSize: 5 }).then(res => {
        this.comments = res.rows
        this.commentTotal = res.total
      })
      checkInteractionStatus('product', this.detail.productId, this.detail.creatorId).then(res => {
        this.isLiked = res.liked
      })
    },
    handleLike() {
      toggleLike('product', this.detail.productId).then(() => {
        this.isLiked = !this.isLiked
        this.$modal.msgSuccess(this.isLiked ? '点赞成功' : '已取消点赞')
      })
    },
    submitComment() {
      if (!this.newComment.trim()) return
      addComment({
        targetType: 'product',
        targetId: this.detail.productId,
        content: this.newComment
      }).then(() => {
        this.$modal.msgSuccess('发表成功')
        this.newComment = ''
        this.loadInteractionData()
      })
    },
    handleShare() {
      addShare('product', this.detail.productId, 'link').then(() => {
        this.$modal.msgSuccess('已生成分享链接（演示）')
      })
    },
    addToCart(product) {
      addCartItem(product, 1)
      this.$modal.msgSuccess('已加入购物车')
    },
    favoriteProduct(product) {
      addPortalFavorite({ targetType: 'product', targetId: product.productId }).then(() => {
        this.$modal.msgSuccess('已收藏商品')
      })
    },
    handleContact(product) {
      this.detailOpen = false;
      const data = {
        targetType: 'product',
        targetId: product.productId
      };
      openSession(data).then(res => {
        // 跳转到聊天页面
        this.$router.push('/portal/chat');
      });
    },
    money(value) {
      return Number(value || 0).toFixed(2)
    }
  }
}
</script>

<style scoped>
.portal-page {
  padding: 20px;
  background: #f5f7fb;
  min-height: calc(100vh - 84px);
}

.toolbar {
  padding: 16px 16px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.product-card {
  min-height: 240px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(15, 35, 52, .06);
}

.cover {
  height: 108px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 42px;
  background: linear-gradient(135deg, #297e7b, #6b9f88);
}

.body {
  padding: 14px;
}

.meta,
.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta span {
  color: #6b7280;
  font-size: 12px;
}

h2 {
  margin: 12px 0 8px;
  color: #1f2937;
  font-size: 17px;
}

p {
  min-height: 44px;
  margin: 0 0 14px;
  color: #6b7280;
  line-height: 1.6;
}

.foot strong {
  color: #d97706;
  font-size: 18px;
}

.interaction-bar {
  margin: 15px 0;
  display: flex;
  gap: 10px;
}

.comment-section {
  margin-top: 25px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.comment-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-item {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #eee;
}

.comment-user {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 5px;
}

.comment-user span {
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
}

.no-comment {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
}
</style>
