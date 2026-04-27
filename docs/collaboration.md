# 文创手作定制交易平台 · AI 协作指南

> 本项目由 Claude / Codex / Gemini 协作开发。本文档给后续接手的 AI 或同学看的：先知道项目做到哪，再知道怎么接着做，最后再查历史记录。

---

## 1. 先看结论

**当前阶段**：阶段 4 · 前台用户端。

阶段 1 到阶段 3 已完成，后台 CRUD、定制业务闭环、买家/创作者角色、后端数据权限、创作者个人中心都已经落地。

下一步优先做：

1. 交易闭环补齐：购物车、收货地址、模拟支付页。
2. 前台体验补齐：创作者主页、收藏中心、社区帖子列表与详情。
3. 推荐算法：用户行为表、基于用户的协同过滤、首页“猜你喜欢”。
4. 管理增强：统计大屏、商品审核、评论审核。
5. 文档测试：E-R 图、接口文档、JUnit、JMeter、答辩 PPT。

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
| 用户 | 注册登录、身份选择、个人信息、收货地址 | 注册登录、买家/创作者选择已完成；个人中心复用系统页 | 补收货地址 |
| 商品 | 分类、创作者发布、搜索浏览、详情 | 分类、发布、上下架、前台商品列表/详情已完成 | 商品详情可补加入购物车 |
| 定制 | 需求发布、列表匹配、报价沟通、进度跟踪 | 需求广场、报价、选中报价、订单状态机已完成；沟通为备注说明版 | 后续可加站内消息 |
| 交易 | 购物车、订单生成、模拟支付、订单状态 | 报价生成订单、订单状态流转已完成 | 优先补购物车和模拟支付 |
| 社区 | 评价、作品分享、点赞评论、关注创作者 | 后台表和管理页已完成，前台展示未完整 | 补社区前台和创作者主页 |
| 后台 | 用户、商品、订单、评论、数据统计 | 基础管理已完成 | 补统计大屏、审核演示 |
| 算法 | 基于内容或行为的推荐 | 尚未实现 | 建议做用户行为 + 协同过滤 |

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
- [ ] 创作者主页：作品列表、关注按钮
- [ ] 收货地址管理
- [ ] 购物车
- [x] 订单中心：买家/创作者两个视角
- [ ] 收藏中心
- [ ] 社区帖子列表与详情
- [ ] 模拟支付页

### 阶段 4.5 · 交易演示闭环

> 这是从开题报告截图倒推出来的最高优先级。答辩时老师最容易问“用户怎么下单、怎么支付、怎么收货”，所以先补这里。

- [ ] 商品详情加入购物车
- [ ] 购物车数量修改、删除、结算
- [ ] 结算页选择收货地址
- [ ] 模拟支付页：余额/二维码/支付成功三种演示状态任选一种即可
- [ ] 支付成功后订单进入 `created` 或 `paid` 状态
- [ ] 订单中心展示支付状态与物流/制作状态

### 阶段 5 · 推荐算法

- [ ] 用户行为收集表：浏览、收藏、下单
- [ ] 基于用户的协同过滤实现
- [ ] 皮尔逊相关系数计算用户相似度
- [ ] 推荐接口
- [ ] 首页“猜你喜欢”
- [ ] 推荐效果样例数据，论文用

### 阶段 6 · 管理后台增强

- [ ] 数据统计大屏：商品数、订单数、销售额、活跃创作者、热门分类
- [ ] 商品审核流
- [ ] 评论审核流
- [ ] 敏感词过滤模块

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

更细的设计与实施记录在 `docs/superpowers/specs/` 和 `docs/superpowers/plans/`。

---

## 6. 未决问题

- [ ] 推荐算法使用“基于内容”还是“协同过滤”？
  - 建议：做协同过滤。外文翻译能对上，论文亮点也更明确。

- [ ] 模拟支付要不要接支付宝/微信沙箱？
  - 建议：先做纯前端模拟支付。毕设演示够用，风险更低。

- [ ] 前台是否独立部署？
  - 建议：继续放在 `ruoyi-ui`，用 `/portal` 路由组。少一个工程，少一堆麻烦。

- [ ] 商品下架是否需要校验进行中订单？
  - 当前订单表没有绑定 `product_id` / `source_id`，暂不做。
  - 后续如果补现货订单，需要增加订单来源字段后再实现。

- [ ] 评论审核与敏感词过滤谁先做？
  - 建议：先做敏感词过滤，再做审核动作。论文和演示都更好讲。

---

## 7. SQL 执行说明

SQL 顺序不要靠猜。完整说明见 [`../sql/README.md`](../sql/README.md)。

新库最短顺序：

```sql
source sql/ry_20260417.sql;
source sql/quartz.sql;
source sql/creative_platform_tables.sql;
source sql/creative_platform_menu.sql;
source sql/creative_creator_audit_upgrade_20260425.sql;
source sql/buyer_role_upgrade_20260425.sql;
source sql/creator_me_menu_20260426.sql;
source sql/register_enable_20260427.sql;
```

如果是已经执行过 `creative_platform_tables.sql` 的旧库，先看 `sql/README.md` 的“旧库升级顺序”。别硬怼，数据库不会因为你勇敢就原谅你。

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
