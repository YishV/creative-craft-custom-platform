# Frontend Menu And Copy Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补齐并统一前端菜单图标、页面快捷入口、主要按钮和空状态文案，消除明显缺失和表达不一致问题，同时保持现有业务逻辑不变。

**Architecture:** 采用“先扫描、后集中修补、最后构建验证”的方式，仅修改 `ruoyi-ui` 中路由元信息和页面可见文案。改动聚焦在菜单配置、`/portal` 页面和少量后台文创页面，避免牵动接口、状态机和权限逻辑。

**Tech Stack:** Vue 2, Vue Router 3, Element UI, RuoYi-Vue front-end build (`vue-cli-service build`)

---

### Task 1: 扫描导航菜单与可见文案

**Files:**
- Modify: `ruoyi-ui/src/router/index.js`
- Modify: `ruoyi-ui/src/views/portal/*.vue`
- Modify: `ruoyi-ui/src/views/creative/**/*.vue`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 记录待修补项**

检查以下内容并记录：

- `ruoyi-ui/src/router/index.js` 中 `/portal` 路由的 `meta.title` 与 `meta.icon`
- `ruoyi-ui/src/views/portal/index.vue` 中快捷入口卡片标题、说明、图标
- `ruoyi-ui/src/views/portal/*.vue` 中主标题、主按钮、空状态文案、提示语
- `ruoyi-ui/src/views/creative/**/*.vue` 中明显缺失或风格冲突的按钮/提示语

- [ ] **Step 2: 形成统一规则**

统一判断标准：

- 导航标题以“简洁、准确”为主，不重复堆词
- 图标优先复用现有 Element / RuoYi 图标语义
- 按钮动词统一使用“查看 / 提交 / 去结算 / 确认支付 / 返回”
- 空状态文案统一先说“暂无什么”，必要时补一句引导

- [ ] **Step 3: 明确不改动区域**

本轮不改：

- 接口路径和接口调用逻辑
- Vuex / 权限控制逻辑
- 后端 SQL 菜单数据
- 与本次菜单和文案无关的布局、样式重构

### Task 2: 修补路由菜单标题与图标

**Files:**
- Modify: `ruoyi-ui/src/router/index.js`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 写出待调整的路由项清单**

重点检查：

- `/portal/index`
- `/portal/products`
- `/portal/cart`
- `/portal/checkout`
- `/portal/payment`
- `/portal/demands`
- `/portal/orders`
- `/portal/community`
- `/portal/chat`
- `/portal/creators`
- `/portal/favorites`

- [ ] **Step 2: 调整标题与图标**

按以下原则修改：

- 避免 `shopping` 同时承担“商品浏览”和“购物车”但无区分时造成混淆
- 确保“社区作品 / 在线沟通 / 收藏关注 / 创作者”图标可直接联想到功能
- 若标题可更自然，直接统一为用户更容易理解的说法

- [ ] **Step 3: 自查路由一致性**

确认修改后：

- `name` 不变
- `path` 不变
- 仅 `meta.title` / `meta.icon` 发生调整

### Task 3: 修补 `/portal` 首页快捷入口与首页文案

**Files:**
- Modify: `ruoyi-ui/src/views/portal/index.vue`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 调整快捷入口卡片文案**

统一 `quickLinks` 中：

- `title`
- `desc`
- `icon`

要求与路由标题尽量一致，但允许首页入口文案更偏行动导向。

- [ ] **Step 2: 调整首页展示区文案**

检查并修补：

- Hero 区副标题
- “最新上架”“开放需求”等分区标题
- 商品和需求卡片的占位说明
- “更多 / 去报价”等入口文案

- [ ] **Step 3: 保持首页行为不变**

确认以下逻辑不改动：

- `loadDashboard()` 的接口调用
- `$router.push(...)` 跳转目标
- `stats` 数据来源

### Task 4: 修补前台页面主标题、按钮和空状态文案

**Files:**
- Modify: `ruoyi-ui/src/views/portal/cart.vue`
- Modify: `ruoyi-ui/src/views/portal/checkout.vue`
- Modify: `ruoyi-ui/src/views/portal/payment.vue`
- Modify: `ruoyi-ui/src/views/portal/products.vue`
- Modify: `ruoyi-ui/src/views/portal/demands.vue`
- Modify: `ruoyi-ui/src/views/portal/orders.vue`
- Modify: `ruoyi-ui/src/views/portal/community.vue`
- Modify: `ruoyi-ui/src/views/portal/post-detail.vue`
- Modify: `ruoyi-ui/src/views/portal/creators.vue`
- Modify: `ruoyi-ui/src/views/portal/favorites.vue`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 移除过于随意的口语化表达**

例如带调侃、聊天式语气但不利于统一风格的页面说明，改为简洁说明，不改变原意。

- [ ] **Step 2: 统一主要按钮动词**

检查并统一：

- 进入下一步的按钮
- 返回列表 / 返回订单中心
- 收藏、关注、评论、支付、报价等动作按钮

- [ ] **Step 3: 统一空状态与占位说明**

确保这些页面中：

- 空列表时有清晰说明
- 必要时给一个下一步入口
- “暂无说明 / 暂无评论 / 暂无数据”表达风格一致

### Task 5: 修补后台文创页中明显不统一的可见文案

**Files:**
- Modify: `ruoyi-ui/src/views/creative/chat/index.vue`
- Modify: `ruoyi-ui/src/views/creative/dashboard/index.vue`
- Modify: `ruoyi-ui/src/views/creative/me/index.vue`
- Modify: `ruoyi-ui/src/views/creative/comment/index.vue`
- Modify: `ruoyi-ui/src/views/creative/product/index.vue`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 只处理明显可见问题**

仅修补以下类型：

- 缺失标题
- 空状态提示生硬
- 审核按钮或说明文案不统一
- 数据为空时的提示语风格不一致

- [ ] **Step 2: 避免扩大范围**

不重写整页表单文案，不调整表格字段名，不改业务提示逻辑。

### Task 6: 构建验证与结果整理

**Files:**
- Modify: `docs/superpowers/plans/2026-05-01-frontend-menu-copy-polish.md`
- Test: `npm.cmd run build:prod`

- [ ] **Step 1: 运行前端生产构建**

Run: `npm.cmd run build:prod`

Expected:

- Build complete
- 无编译错误
- 如有体积告警可接受，但不能有语法错误或模块缺失

- [ ] **Step 2: 回看关键页面文案**

人工复核：

- 导航菜单
- `/portal` 首页快捷入口
- 商品、需求、订单、支付、社区、创作者、收藏页
- 后台文创聊天、看板、创作者主页等关键页

- [ ] **Step 3: 记录完成结果**

整理输出：

- 发现了哪些缺失或不一致项
- 实际补了哪些图标和文案
- 构建验证结果如何
