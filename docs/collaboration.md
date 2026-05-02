# 文创手作定制交易平台 · AI 协作指南

> 本项目由 Claude / Codex / Gemini 协作开发。本文档给后续接手的 AI 或同学看的：先知道项目做到哪，再知道怎么接着做，最后再查历史记录。

---

## 1. 先看结论

**当前阶段**：阶段 6 全部完成。下一站阶段 7 · 文档测试。

阶段 1 ~ 6 已落地：后台 CRUD、定制业务闭环、买家/创作者角色与数据权限、创作者个人中心、前台门户、购物车、模拟支付、社区收藏、实时聊天、数据看板、动态敏感词过滤、商品/评论审核流均可演示。

阶段 5（推荐算法）已 ❌ 取消，论文方向调整。

下一步优先做：

1. 阶段 7 · 文档测试：E-R 图、Swagger 接口文档、JUnit、JMeter、答辩 PPT。

---

## 2. 接手任务怎么做

每次接手一个任务，按下面 5 步走。少一步都容易把项目搞乱，嗯，别逞强。

1. **先看状态**
   - 执行 `git status --short`，确认当前有没有别人留下的改动。
   - 阅读本文件第 4 节任务清单，找到要做的阶段。
   - 如果任务已有 `(@Claude WIP)` / `(@Codex WIP)` / `(@Gemini WIP)`，先不要抢。

2. **认领任务**
   - 在第 4 节对应任务后面标记：`(@Claude WIP)`、`(@Codex WIP)` 或 `(@Gemini WIP)`。
   - 如果是跨模块任务，先在第 6 节写一句设计说明。

3. **开发与验证**
   - 后端改动至少跑：
     ```powershell
     mvn -pl ruoyi-admin -am -DskipTests package
     ```
   - 前端改动至少跑：
     ```powershell
     cd ruoyi-ui
     npm.cmd run build:prod
     ```
   - 有单测就优先跑相关单测，不要上来就全量轰炸。

4. **更新协作文档**
   - 完成后把任务勾选为 `[x]`。
   - 删除任务后面的 WIP 标记。
   - 在第 5 节追加记录：日期、任务、主要改动、验证方式、完成人、备注。

5. **提交规范**
   - commit message 末尾带完成人，例如：
     ```text
     feat(creative): 创作者认证审核流程 [Codex]
     ```

---

## 3. 项目路线

毕业设计题目：**基于 SpringBoot 的文创手作定制交易平台设计与实现**。

开题报告对应功能：

| 模块 | 开题要求 | 当前状态 | 下一步 |
|---|---|---|---|
| 用户 | 注册登录、身份选择、个人信息、收货地址 | 注册登录、买家/创作者选择已完成；个人中心复用系统页；结算页支持收货信息快照 | 后续可补地址簿 |
| 商品 | 分类、创作者发布、搜索浏览、详情 | 分类、发布、上下架、前台商品列表/详情、加入购物车已完成 | 可继续优化商品详情页 |
| 定制 | 需求发布、列表匹配、报价沟通、进度跟踪 | 需求广场、报价、选中报价、订单状态机已完成；实时聊天 `/portal/chat` 已落地 | 后续可加聊天消息搜索 |
| 交易 | 购物车、订单生成、模拟支付、订单状态 | 购物车、结算、商品订单生成、模拟支付、订单状态流转已完成基础演示版 | 后续可补真实支付或售后 |
| 社区 | 评价、作品分享、点赞评论、关注创作者 | 社区作品列表、详情评论、商品收藏、作品收藏、关注创作者、收藏中心已完成基础演示版 | 后续可补点赞计数 |
| 后台 | 用户、商品、订单、评论、数据统计 | 基础管理 + 数据看板 + 敏感词词库 + 商品/评论审核流已完成 | 阶段 6 收尾 |
| 算法 | 基于内容或行为的推荐 | 协同过滤已取消；动态敏感词过滤（DFA + 词库后台）已落地 | 后续可补审核流复用同一拦截器 |

说明：本项目是毕设演示系统，安全能力不作为当前优先开发项。后续只保留必要的权限边界和演示稳定性，别又把精力全砸到安全配置里，没必要。

核心业务状态：

```text
需求：draft -> published -> quoting -> selected -> closed
报价：pending -> selected / rejected
订单：created -> making -> shipped -> finished
订单也可以：created/making -> cancelled
```

---

## 4. 分阶段任务清单

### 阶段 1 · 后台 CRUD 全量补齐

- [x] creator 页面 CRUD
- [x] post 页面 CRUD
- [x] comment 页面 CRUD
- [x] favorite 页面 CRUD，按 targetType 动态目标下拉
- [x] product / demand / quote / order 关联字段改名称展示
- [x] 前端 `build:prod` 验证通过

### 阶段 2 · 业务闭环

- [x] 定制需求状态机
- [x] 报价状态机
- [x] 选中报价后自动生成订单
- [x] 订单开始制作、发货、完成、取消
- [x] 前端接入业务按钮
- [x] 商品上下架业务校验
- [x] 创作者认证审核流程

### 阶段 3 · 角色与权限

- [x] 注册默认 `buyer`，审核通过追加 `creator`
- [x] 管理员、买家、创作者三类角色语义明确
- [x] 买家只看自己的需求、订单、收藏
- [x] 创作者只看自己的商品、报价、订单
- [x] `@CreativeDataScope` + AOP 注解化数据权限
- [x] 创作者主页：`GET /creative/creator/me` + 前端页面

### 阶段 4 · 前台用户端

- [x] 注册登录入口 + 身份选择（买家/创作者）
- [x] 前台路由组：建议使用 `/portal/*`
- [x] 首页：推荐位、热门分类、最新作品
- [x] 商品列表 / 商品详情
- [x] 需求广场：买家发布需求，创作者看需求并报价
- [x] 创作者主页：创作者列表、关注按钮
- [x] 收货地址管理
- [x] 购物车
- [x] 订单中心：买家/创作者两个视角
- [x] 收藏中心
- [x] 社区帖子列表与详情
- [x] 模拟支付页

### 阶段 4.5 · 交易演示闭环

> 这是从开题报告截图倒推出来的最高优先级。答辩时老师最容易问"用户怎么下单、怎么支付、怎么收货"，所以先补这里。

- [x] 商品详情加入购物车
- [x] 购物车数量修改、删除、结算
- [x] 结算页填写收货地址
- [x] 模拟支付页：二维码样式 + 支付成功
- [x] 支付成功后订单 `pay_status` 进入 `paid` 状态
- [x] 订单中心展示支付状态与物流/制作状态

### 阶段 4.6 · 实时在线沟通

> 开题报告里"报价沟通"原本只用备注承载，老师可能追问"买家和创作者怎么聊"。补上 WebSocket 聊天作为答辩亮点。

- [x] 聊天表结构：`creative_chat_session` + `creative_chat_message`
- [x] 聊天领域模型 + Mapper
- [x] 聊天服务核心逻辑（开启会话、收发消息、标记已读）
- [x] REST 接口：`/portal/chat/session/list`、`/session`、`/message/list`、`/message`、`/session/{id}/read`
- [x] WebSocket 端点：`/ws/chat`（query token 鉴权 + 多端会话广播）
- [x] 前端 `views/creative/chat/index.vue`：会话列表 + 消息历史 + 输入框 + 图片上传
- [x] 实时收发与图片消息修复

### 阶段 5 · 推荐算法 ❌ 已取消

> 论文方向调整：外文翻译《Online Shopping Mall Based on Collaborative Filtering》原本对齐协同过滤，后决定不做。改为以"动态敏感词过滤"作为唯一论文算法亮点（外文翻译的第二个亮点）。本阶段保留行号便于追溯，不要再拾起这些任务。

- [x] ~~用户行为收集表~~ 已取消
- [x] ~~基于用户的协同过滤实现~~ 已取消
- [x] ~~皮尔逊相关系数计算用户相似度~~ 已取消
- [x] ~~推荐接口~~ 已取消
- [x] ~~首页"猜你喜欢"~~ 已取消
- [x] ~~推荐效果样例数据~~ 已取消

### 阶段 6 · 管理后台增强

- [x] 数据统计大屏：商品数、订单数、销售额、活跃创作者、热门分类
- [x] 商品审核流（pending/approved/rejected + 上架前必须 approved + 驳回自动下架）
- [x] 评论审核流（旧版 creative_comment 表 audit_status：pending/approved/rejected，后台通过/驳回按钮）
- [x] 敏感词过滤模块（DFA 算法 + 词库 CRUD + 评论/聊天入口拦截）

### 阶段 7 · 文档与测试

- [ ] E-R 图
- [ ] 系统架构图、用例图
- [ ] 注册、定制、报价选中、订单流转流程图
- [ ] Swagger / Springdoc 接口文档
- [ ] 核心 Service 单元测试
- [ ] JMeter 压测报告
- [ ] 演示数据：创作者、商品、需求、报价、订单各不少于 10 条
- [ ] 答辩 PPT 大纲

---

## 5. 已完成记录

| 日期 | 任务 | 主要改动 | 验证 | 完成人 | 备注 |
|---|---|---|---|---|---|
| 2026-04-25 | 项目路线对齐 | README、协作文档、阶段路线 | 阅读资料 | Claude | 根据开题报告整理方向 |
| 2026-04-25 | 后台 CRUD 补齐 | creator/post/comment/favorite/product/demand/quote/order 前端页面 | 前端 build 通过 | Claude | 后台管理基础可用 |
| 2026-04-25 | README 重写 | 背景、目标、技术栈、启动说明、状态机 | 文档检查 | Claude | 面向毕设说明 |
| 2026-04-25 | 状态机与业务闭环 | `CreativeStatusFlow`、需求/报价/订单 Service 与 Controller | 后端打包通过 | Claude | 选中报价自动生成订单 |
| 2026-04-25 | 前端业务按钮 | 报价选中、订单推进按钮 | 前端 build 通过 | Claude | 后台可操作业务流 |
| 2026-04-25 | 商品上下架校验 | 商品上下架接口、Service 校验、前端按钮 | 单测、后端打包、前端 build | Codex | 上架前校验名称、价格、创作者、分类 |
| 2026-04-25 | 创作者认证审核 | 申请、通过、驳回、追加 creator 角色 | 单测、后端打包、前端 build | Codex | 新增审核字段与增量 SQL |
| 2026-04-25 | 买家角色自动绑定 | 注册后绑定 `buyer`，历史 common 用户回填 | 单测、后端打包、前端 build | Codex | 新增 `buyer` 角色 SQL |
| 2026-04-25 | 后端数据权限 | 统一权限服务，收敛买家/创作者数据范围 | 单测、后端打包、前端 build | Codex | 防止只靠前端隐藏 |
| 2026-04-26 | 创作者个人中心 | `CreativeCreatorProfile`、`/creative/creator/me`、前端页面、菜单 SQL | 相关单测、后端打包、前端 build | Claude | 创作者可看档案与统计 |
| 2026-04-26 | 注解化数据权限 | `@CreativeDataScope`、AOP 切面、Service 简化 | 相关单测、后端打包 | Claude | 重复权限模板收敛 |
| 2026-04-27 | 协作文档与 SQL 顺序整理 | `docs/collaboration.md`、`sql/README.md`、README 数据库段落 | 文档检查 | Codex | 让后续接手更容易 |
| 2026-04-27 | 创作者申请表单去除用户 ID 输入 | `CreativeCreatorController.java`、`views/creative/creator/index.vue` | 后端 package、前端 build | Codex | 新增申请自动绑定当前登录用户，页面显示“申请账号”而不是要求填写用户 ID |
| 2026-04-27 | Review 业务边界处理 | `CreativeOrderServiceImpl.java`、`CreativeQuoteServiceImpl.java` 及相关测试 | 订单/报价单测、后端 package | Codex | 订单动作按买家/创作者角色限制；报价校验需求存在和状态 |
| 2026-04-27 | 前台用户端基础页面 | `CreativePortalController.java`、`api/creative/portal.js`、`views/portal/*`、`router/index.js` | 后端 package、前端 build | Codex | 新增 `/portal` 首页、商品浏览、需求广场、订单中心；前台商品/需求使用只读门户接口，避免被后台数据权限挡住 |
| 2026-04-27 | 注册登录入口与身份选择 | `views/login.vue`、`views/register.vue`、`sql/register_enable_20260427.sql`、`sql/ry_20260417.sql` | 后端 package、前端 build | Codex | 登录页显示“立即注册”；注册页提供买家/创作者选择；SQL 默认开启自助注册，创作者注册后走认证申请流程 |
| 2026-04-27 | 开题需求对齐文档 | `README.md`、`docs/collaboration.md` | 文档检查 | Codex | 按开题截图补充需求对照表、阶段 4.5 交易闭环任务，并修正 README 当前进度 |
| 2026-04-28 | 交易演示闭环 | `CreativePortalController.java`、`CreativeOrderServiceImpl.java`、`CreativeOrderMapper.xml`、`views/portal/{products,cart,checkout,payment,orders}.vue`、`sql/order_payment_upgrade_20260428.sql` | 订单单测、后端 package、前端 build | Codex | 商品加入购物车，本地购物车结算，提交收货信息生成商品订单，模拟支付后订单标记已支付 |
| 2026-04-28 | 前台社区与收藏 | `CreativePortalController.java`、`api/creative/portal.js`、`views/portal/{community,post-detail,creators,favorites}.vue`、`router/index.js` | 后端 package、前端 build | Codex | 新增社区作品列表/详情、评论、商品/作品收藏、关注创作者、收藏中心 |
| 2026-04-28 | 前台入口与文档状态整理 | `views/portal/index.vue`、`README.md`、`docs/collaboration.md`、`sql/README.md`、`docs/project-review.md` | 前端 build、文档检查 | Codex | 首页补购物车、社区、创作者、收藏快捷入口；文档同步到阶段 5 |
| 2026-04-28 | SQL 安装包整合 | `sql/install/01-05_*.sql`、`sql/README.md` | SQL 检查 | Codex | 把零散历史补丁合并为 5 个安装文件；移除推荐算法相关后续建议（commit `9e8412f`） |
| 2026-04-28 | 实时在线沟通 | `creative_chat_session/message` 表、`CreativeChatSession/Message/Request` 领域模型与 Mapper、`ICreativeChatService` + Impl、`CreativeChatController`(`/portal/chat/*`)、`web/websocket/CreativeChatWebSocketConfig` + `Handler`(`/ws/chat`)、`api/creative/chat.js`、`views/creative/chat/index.vue` | 后端 package、前端 build | Codex / Gemini | WebSocket token 鉴权 + 多端会话广播；REST 兜底；Gemini 在 `8f7f294` 参与了细节调整 |
| 2026-04-29 | 实时聊天问题修复 | `CreativeChatWebSocketHandler.java`、`views/creative/chat/index.vue`（图片上传与 socket 重连） | 后端 package、前端 build | Codex | 修复实时收发消息和图片消息（commit `16cc9ed`） |
| 2026-05-01 | 数据统计看板 | `CreativeDashboardController/Service/Mapper`、`CreativeDashboardStats` DTO、`mapper/creative/CreativeDashboardMapper.xml`、`api/creative/dashboard.js`、`views/creative/dashboard/index.vue`、`sql/install/04_system_menus.sql`、`sql/dashboard_menu_upgrade_20260501.sql` | 后端 mvn package、前端 build:prod | Claude | 顶级菜单"数据看板"，6 卡（商品/订单/GMV/活跃创作者/热门分类数/支付率）+ 近 7 日订单趋势折柱混合图 + 热门分类 Top5 饼图 + 活跃创作者 Top5 表格；权限 `creative:dashboard:view`，admin 默认可见 |
| 2026-05-01 | 动态敏感词过滤 | `SensitiveWord/SensitiveCheckResult` DTO、`SensitiveWordMapper(.xml)`、`ISensitiveWordService` + `SensitiveWordServiceImpl`(DFA + AtomicReference 热替换)、`SensitiveWordController`(/creative/sensitive/* CRUD + check + reload)、`api/creative/sensitive.js`、`views/creative/sensitive/index.vue`(列表 + 在线检测对话框)、`CreativeChatServiceImpl#sendMessage`、`CreativeCommentServiceImpl#insertCreativeComment`、`CreativeInteractionServiceImpl#insertComment` 调用 `enforceClean` 拦截、`sql/install/03_social_interaction.sql`、`sql/install/04_system_menus.sql`、`sql/sensitive_word_upgrade_20260501.sql` | 后端 mvn package、前端 build:prod | Claude | 论文核心亮点：DFA 树构建 O(L)、匹配 O(N\*K)；CRUD 后内部 reload；后台支持在线检测命中词 + 替换为 \*；初始词库 8 条（涉政/辱骂/广告） |
| 2026-05-02 | 新增部署运行手册 | `docs/setup.md`：环境要求、JAVA_HOME 配置、数据库/Redis 初始化、IDE/命令行两种后端启动方式、前端 dev/build、三个登录入口与默认账号、Nginx 部署示例、FAQ、交付清单。路径用 `<项目根目录>` `<JDK17 安装目录>` 等占位符，不绑定本机 | 文档检查 | Claude | 面向交付：第三方接手按本文一步步即可起项目 |
| 2026-05-02 | 隔离买家/卖家侧边栏 + 修复 creator 角色 key 错配 | `04_system_menus.sql`：role_key 'seller' → 'creator'（与 `CreativeCreatorServiceImpl.CREATOR_ROLE_KEY` 对齐，否则审核通过会抛"未找到角色[creator]"）；菜单关联补全（买家+作品分享，卖家+定制需求/订单）；注释写清"买家/卖家只关联 2000+ 业务菜单，系统管理仅 user_id=1 admin 走 isAdmin 走全量返回" | 后端 mvn package | Claude | 当前 SQL 配置已确保非 admin 用户登录后侧边栏看不到系统管理/监控/工具/项目说明 |
| 2026-05-02 | 部门改造为买家/卖家两条 | `01_framework_base.sql` 删除若依 9 条预置部门（100-109）+ admin/ry 的 dept_id 改 200；`04_system_menus.sql` 把"前台用户"拆为买家(200) + 卖家(201)；`RegisterBody` 新增 `identityType` 字段（前端早就传了但后端没接）；`SysRegisterService` 按 identityType=creator 落 201、否则 200 | 后端 mvn package | Claude | 之后用户管理页左边部门树只剩两条，admin 一眼看清买家 vs 创作者群体 |
| 2026-05-02 | 拆分买家/创作者/管理员登录入口 + 内嵌注册 | `router/index.js` 新增 `/buyer/login`、`/creator/login` 路由（meta.mode 区分模式）；`permission.js` 白名单 + 已登录拦截兼容三入口；`views/login.vue` 重写为 mode-driven：顶部品牌区 + 登录/注册 tab 切换（admin 模式禁用注册 tab），按 mode 切换标题/副标题/默认跳转/按钮主题/背景渐变；注册成功后自动切回登录 tab 并填入账号；identityType 由当前入口的 mode 写死（buyer/creator） | 前端 build:prod | Claude | URL 三选一：`/login` 管理员（admin/admin123） / `/buyer/login` 买家（跳 `/portal/index`） / `/creator/login` 创作者（跳 `/creative/creator/me`）。底层共用同一组件，注册并入登录页 tab，独立 `/register` 路由保留兜底 |
| 2026-05-02 | 修复 buyer 角色重复 + 注册无部门 | 删除 `01_framework_base.sql:131` 重复的 role_id=4 买家角色（与 04_system_menus 的 100 重复且 role_key 同为 buyer，导致 `checkRoleKeyUnique` LIMIT 1 命中残角色 4）；`04_system_menus.sql` 新增前台用户部门 dept_id=200；`SysRegisterService.register` 注册时默认 `setDeptId(200L)` | mvn -pl ruoyi-framework -am 通过 | Claude | 之前 dept_id 为 null，admin 在用户管理按部门筛选时看不到新注册账号 |
| 2026-05-02 | 前台首页快捷菜单标题不显示 | `views/portal/index.vue`：`<button>` 容器换成 `<div role="button">` + flex 列布局；新增 `.quick-title`/`.quick-desc` 类，提高 min-height | 前端 build:prod | Claude | 原 `<button>` 内嵌 block 子元素被浏览器折叠，导致"购物车/在线沟通/创作者/收藏关注"标题与描述都不渲染 |
| 2026-05-02 | 创作者数据隔离：作品分享/评论互动按 creatorId 自动过滤；分类管理菜单从 creator 撤掉 | `CreativePostMapper.xml` 加 `creatorId` 过滤；`CreativePostServiceImpl#selectCreativePostList` 加 `@CreativeDataScope(CREATOR, "creatorId")`；`CreativeComment` 加 `ownerCreatorId` 字段，`CreativeCommentMapper.xml` 用 `exists(select 1 from creative_post p where p.post_id=c.post_id and p.creator_id=#{ownerCreatorId})` 过滤；`CreativeCommentServiceImpl#selectCreativeCommentList` 加 `@CreativeDataScope(CREATOR, "ownerCreatorId")`；`sql/install/04_system_menus.sql` 卖家菜单移除 `2002`；新增 `sql/creator_role_data_scope_20260502.sql` 增量补丁；`sql/README.md` 同步 | 后端 `mvn -pl ruoyi-system -am compile` 通过 | Claude | admin 透传看全部，creator 切面自动注入 creatorId/ownerCreatorId；收藏关注页本来就有 `@CreativeDataScope BUYER`，无需变动 |
| 2026-05-02 | 修复创作者越权：撤掉 creator 角色"创作者管理"管理员菜单 | `sql/install/04_system_menus.sql` 移除 `(101, 2001) (101, 2100) (101, 2101) (101, 2102)`；新增补丁 `sql/creator_role_revoke_admin_menu_20260502.sql`；`sql/README.md` 补丁清单同步 | SQL 检查 | Claude | 之前 creator 登录后能看到所有创作者档案并能审核/编辑别人，因为 `CreativeCreatorServiceImpl.selectCreativeCreatorList` 没按 userId 过滤、且菜单关联把管理员入口下放给了 creator。修复后创作者只能通过 `/creative/creator/me`（getMyProfile 按当前 userId 取）维护自己档案 |
| 2026-05-02 | 收藏页空值兜底 + 买家直达商品 + 中转 title + 批量取消 | 后端 `CreativeFavoriteMapper.xml` join 不到时给 `target_name`/`target_subtitle`/`target_cover` 填"商品已下架/创作者已注销/作品已删除"等兜底文案；前端 `views/login.vue` buyer `defaultRedirect` 由 `/portal/index` 改为 `/portal/products`，`router/index.js` portal 根 redirect 同步改、portal/index `hidden:true`，`views/index.vue` 同步改 buyer 跳商品并设 `document.title='正在跳转...'` 防闪；`views/portal/favorites.vue` 卡片加 checkbox + 顶部"全选/已选数/批量取消"工具栏，`Promise.all` 并发调 `cancelPortalFavorite` | 前端 build:prod | Claude | 完成 ToDo #2/#6/#7/#9，#3/#10/#12 等仍待办 |
| 2026-05-02 | 去除通用首页，登录后按角色直达目标页 | `views/index.vue` 改为薄壳：根据 `$store.getters.roles` 替换跳转（admin → `/creative-dashboard`、creator → `/creative/creator/me`、buyer → `/portal/index`）；`router/index.js` 把根路由 `path:''` 加 `hidden:true`、子项 `affix:false`，去掉侧边栏与固定 tab 的"首页"项；`views/login.vue` admin 模式 `defaultRedirect` 由 `/` 改为 `/creative-dashboard` | 前端 build:prod | Claude | 之前 admin 登录看到一个静态项目介绍页，现在直接进数据看板；任何角色访问 `/` 也会被 RoleRedirect 转到对应入口 |
| 2026-05-02 | 收藏关注页改卡片化（封面+标题+副标题+备注） + 跳详情 | `CreativeFavorite` 加 `targetSubtitle`/`targetCover`；`CreativeFavoriteMapper.xml` 用 case+concat 按类型拼副标题（商品=￥价格·店铺、创作者=等级·名、作品=类型·店铺）和首字符封面，多 join 一份 `creative_creator` 取 product/post 的创作者名；`views/portal/favorites.vue` 表格换成 grid 卡片，按类型不同色头；查看跳转：商品 → `/portal/products?productId=xxx`，`products.vue` 在 `created` 检查 query.productId 自动 `showDetail` 打开详情对话框；作品/创作者跳已有详情/详情筛选 | 后端 `mvn -pl ruoyi-system -am compile`、前端 build:prod | Claude | 之前列表只显示一个名字太单调；"查看"原本只跳列表页，现在跳到具体详情 |
| 2026-05-02 | 收藏关注页改显示名称 + 作品详情页加收藏按钮 | `views/portal/favorites.vue`：把"目标ID"列换成"名称"列，渲染 mapper 已 join 的 `targetName`（商品名/创作者名/作品标题），点击跳转目标页；`views/portal/post-detail.vue`：新增"收藏作品"按钮，复用 `/portal/favorite` 接口（targetType=post），按 (post, postId) 查 favoriteId 维护切换状态 | 前端 build:prod 通过 | Claude | 之前列表只能看到裸 ID，且作品页只有点赞（写 `creative_interaction_like`），没有"收藏"动作，所以"收藏的作品"页一直空 |
| 2026-05-02 | 修复关注创作者后"收藏关注"列表查不到 | `CreativeFavoriteMapper(.java/.xml)` 新增 `deleteByUserAndTarget` / `countByUserAndTarget`；`CreativeInteractionServiceImpl#toggleFollow` 同步维护 `creative_favorite_follow`（targetType=creator）：关注时若不存在则插入收藏记录，取消关注时按 (userId, creator, creatorId) 删除 | 后端 `mvn -pl ruoyi-system -am compile` 通过 | Claude | 根因：关注按钮写 `creative_interaction_follow`，"收藏关注"页查 `creative_favorite_follow`，两表不互通 |
| 2026-05-01 | 商品/评论审核流 | `CreativeProduct` 加 audit_status/remark/by/time、`CreativeProductMapper.xml`、`ICreativeProductService` + Impl 加 submitAudit/approveAudit/rejectAudit、`CreativeProductController` 加三个 POST 端点、`ICreativeCommentService` + `CreativeCommentServiceImpl` 加 approveAudit/rejectAudit、`CreativeCommentController` 加 POST 端点、`api/creative/{product,comment}.js`、`views/creative/{product,comment}/index.vue` 加通过/驳回按钮 + 审核状态列、`sql/install/02_business_core.sql`、`sql/install/04_system_menus.sql`、`sql/product_comment_audit_upgrade_20260501.sql` | 后端 mvn package、前端 build:prod | Claude | 商品状态机：pending/approved/rejected；上架前必须 approved；驳回必须填理由且自动下架；评论同样 pending/approved/rejected。审核权限 `creative:product:audit` / `creative:comment:audit`，菜单 2114/2115 |

更细的设计与实施记录在 `docs/superpowers/specs/` 和 `docs/superpowers/plans/`。

---

## 6. 未决问题

- [x] 推荐算法做"基于内容"还是"协同过滤"？
  - 已定：**都不做**，论文方向调整。仅保留"动态敏感词过滤"作为算法亮点。

- [x] 模拟支付要不要接支付宝/微信沙箱？
  - 已定：做纯前端模拟支付。毕设演示够用，风险更低。

- [x] 前台是否独立部署？
  - 已定：继续放在 `ruoyi-ui`，用 `/portal` 路由组。少一个工程，少一堆麻烦。

- [ ] 商品下架是否需要校验进行中订单？
  - 订单表已补 `source_type` / `source_id`，后续可以校验商品是否存在未完成订单。

- [x] 敏感词过滤的拦截范围？
  - 已落地：评论提交（`CreativeCommentServiceImpl` 旧版表 + `CreativeInteractionServiceImpl` 通用表）+ 聊天文本（`CreativeChatServiceImpl#sendMessage`，仅 text 类型）。
  - 命中即抛 `ServiceException`，前端 `$modal.msgError` 自动展示。
  - 可选未做：作品标题、商品描述。

---

## 7. SQL 执行说明

SQL 顺序不要靠猜。完整说明见 [`../sql/README.md`](../sql/README.md)。

零散历史补丁已经合并为 `sql/install/` 下的 5 个安装文件。

**新库**按编号顺序执行：

```sql
source sql/install/01_framework_base.sql;       -- 若依基础 + Quartz + 注册开关
source sql/install/02_business_core.sql;        -- 创作者/分类/商品/需求/报价/订单/支付
source sql/install/03_social_interaction.sql;   -- 作品/评论/互动/聊天/品牌去标识
source sql/install/04_system_menus.sql;         -- 菜单 + 角色 + 权限（含数据看板顶级菜单）
source sql/install/05_test_data.sql;            -- 演示数据（可选）
```

**已经跑过 04 的旧库**追加数据看板菜单 / 敏感词模块 / 商品评论审核：

```sql
source sql/dashboard_menu_upgrade_20260501.sql;
source sql/sensitive_word_upgrade_20260501.sql;
source sql/product_comment_audit_upgrade_20260501.sql;
```

如果你手上是更早期的库（执行过散文件 `creative_platform_tables.sql` 等），重导一遍 `01-04` 最稳；想原地升级先看 `sql/README.md`。别硬怼，数据库不会因为你勇敢就原谅你。

---

## 8. 常用命令

```powershell
# 当前改动
git status --short

# 后端打包
mvn -pl ruoyi-admin -am -DskipTests package

# 前端构建
cd ruoyi-ui
npm.cmd run build:prod

# 启动后端
.\bin\run-admin-jdk17.bat

# 启动前端
cd ruoyi-ui
npm.cmd run dev

# 检查端口占用
Get-NetTCPConnection -State Listen | Where-Object { $_.LocalPort -in 80,8080 }
```
