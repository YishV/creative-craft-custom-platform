<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="14" class="intro-col">
        <h2>文创手作定制交易平台</h2>
        <p>
          本项目用于承载毕业设计“基于 SpringBoot 的文创手作定制交易平台设计与实现”。当前版本已完成注册登录、身份区分、商品浏览、定制需求、报价沟通、购物车、模拟支付、订单流转、社区作品、评论互动与收藏关注等核心演示流程。
        </p>
        <p>
          <b>当前版本：</b><span>毕设演示版 v{{ version }}</span>
        </p>
        <p>
          <el-tag type="success">交易闭环基础完成</el-tag>
          <el-tag type="info">下一步：推荐算法与统计大屏</el-tag>
        </p>
        <p>
          <el-button type="primary" size="mini" icon="el-icon-s-shop" @click="goRoute('/portal/index')">进入文创前台</el-button>
          <el-button size="mini" icon="el-icon-shopping-cart-full" @click="goRoute('/portal/cart')">查看购物车</el-button>
          <el-button size="mini" icon="el-icon-data-analysis" @click="goRoute('/portal/orders')">订单中心</el-button>
        </p>
      </el-col>

      <el-col :sm="24" :lg="10" class="intro-col">
        <h2>功能入口</h2>
        <el-row :gutter="12" class="quick-links">
          <el-col :span="12" v-for="item in quickLinks" :key="item.path">
            <div class="quick-card" @click="goRoute(item.path)">
              <i :class="item.icon"></i>
              <span>{{ item.title }}</span>
            </div>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card">
          <div slot="header" class="clearfix">
            <span>平台定位</span>
          </div>
          <div class="body">
            <p><i class="el-icon-s-custom"></i> 目标用户：买家、创作者、平台管理员</p>
            <p><i class="el-icon-s-order"></i> 核心流程：浏览商品、发布需求、报价沟通、订单成交</p>
            <p><i class="el-icon-star-on"></i> 特色方向：定制交易、社区互动、个性化推荐</p>
            <p><i class="el-icon-data-analysis"></i> 当前阶段：前台交易与社区演示闭环已具备</p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card">
          <div slot="header" class="clearfix">
            <span>演示流程</span>
          </div>
          <el-timeline>
            <el-timeline-item timestamp="买家" placement="top">
              注册登录后浏览商品、加入购物车，或在需求广场发布定制需求。
            </el-timeline-item>
            <el-timeline-item timestamp="创作者" placement="top">
              维护创作者档案，发布商品，查看需求并提交报价。
            </el-timeline-item>
            <el-timeline-item timestamp="交易" placement="top">
              买家结算商品或选中报价生成订单，模拟支付后推进制作、发货、完成。
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="info-card">
          <div slot="header" class="clearfix">
            <span>下一步补充</span>
          </div>
          <div class="body">
            <p><b>推荐算法：</b>记录浏览、收藏、下单行为，计算用户相似度。</p>
            <p><b>统计大屏：</b>展示订单数、销售额、热门分类、活跃创作者。</p>
            <p><b>演示数据：</b>补足商品、需求、报价、订单、社区作品样例。</p>
            <p><b>论文材料：</b>补 E-R 图、流程图、接口说明和测试记录。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      version: "1.0.0",
      quickLinks: [
        { title: "商品浏览", path: "/portal/products", icon: "el-icon-goods" },
        { title: "需求广场", path: "/portal/demands", icon: "el-icon-document" },
        { title: "社区作品", path: "/portal/community", icon: "el-icon-chat-dot-square" },
        { title: "收藏关注", path: "/portal/favorites", icon: "el-icon-star-off" }
      ]
    }
  },
  methods: {
    goRoute(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  .intro-col {
    padding-left: 20px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;
    line-height: 1.8;

    b {
      font-weight: 700;
    }
  }

  .el-tag {
    margin-right: 8px;
  }

  .quick-links {
    margin-top: 8px;
  }

  .quick-card {
    display: flex;
    align-items: center;
    height: 58px;
    margin-bottom: 12px;
    padding: 0 16px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;

    i {
      margin-right: 10px;
      font-size: 20px;
      color: #409eff;
    }

    &:hover {
      color: #409eff;
      border-color: #b3d8ff;
      background: #ecf5ff;
    }
  }

  .info-card {
    min-height: 250px;

    .body i {
      margin-right: 6px;
      color: #409eff;
    }
  }
}
</style>
