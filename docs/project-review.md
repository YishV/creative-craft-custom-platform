# 项目 Review 记录

> Review 日期：2026-04-27  
> 范围：项目结构、文创业务核心 Service/Controller、配置文件、协作文档、SQL 脚本。

---

## 总体评价

项目基于 RuoYi-Vue 改造，后台 CRUD、定制需求/报价/订单闭环、买家/创作者角色、后端数据权限、前台门户、购物车、模拟支付、社区和收藏已经形成演示闭环。AI 协作推进速度快，文档和阶段记录也比一般毕设项目更完整。

安全项按毕设演示项目处理，仅保留为建议；业务边界问题已经优先处理。当前更值得补的是推荐算法、统计大屏、演示数据和论文配套图表。

---

## 高优先级问题

### 1. 配置文件含明文公网数据库账号密码

位置：`ruoyi-admin/src/main/resources/application-druid.yml:9-11`

当前配置直接写了公网数据库地址、用户名、密码。只要仓库被分享、截图、上传网盘或交给答辩机器，凭据就等于暴露。

状态：按演示项目处理，暂不强制修改。

后续建议：

- 立即更换数据库密码。
- 提供 `application-druid.example.yml` 作为模板，真实配置不要提交。

### 2. JWT 密钥使用默认固定值

位置：`ruoyi-admin/src/main/resources/application.yml:95-101`

`token.secret` 还是固定示例值。别人拿到源码后可以推测签名密钥，伪造或长期复用 token 的风险会上升。

状态：按演示项目处理，暂不强制修改。

后续建议：

- 本地开发使用 `.env` 或 IDE 启动参数。
- 生产/演示环境使用随机长字符串。

### 3. 文创接口没有进入 XSS 过滤范围

位置：`ruoyi-admin/src/main/resources/application.yml:140-147`

XSS 过滤只匹配 `/system/*,/monitor/*,/tool/*`，文创业务 `/creative/*` 不在范围里。评论、作品、需求、商品名称等内容后续会直接面向用户展示，属于高风险输入。

状态：按演示项目处理，暂不强制修改。

后续建议：

- 评论、作品正文这类富文本如果未来需要保留格式，要单独做白名单清洗，不要直接信任前端。

### 4. 订单状态流转没有区分买家和创作者动作

位置：`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeOrderServiceImpl.java:103-117`

`transitOrderStatus` 只调用 `ensureOrderOwned`，只要当前用户是买家或卖家，就能执行所有订单动作。结果是买家可以“开始制作/发货”，创作者也可以“确认完成”。这会破坏定制交易流程。

状态：已处理。订单状态推进已按目标状态区分角色：开始制作、发货只能由创作者执行；完成只能由买家执行；管理员仍可兜底处理。

后续建议：

- `cancelled` 至少要按状态和角色细分，例如未制作前买家可取消，制作中取消需要后台或双方确认。
- 单测继续覆盖管理员兜底和取消规则。

### 5. 报价创建允许挂到非可报价需求上

位置：`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeQuoteServiceImpl.java:50-70`

创建报价时，如果需求不存在、处于草稿、已选中或已关闭，当前逻辑仍可能插入报价；只有需求是 `published` 时才顺手改成 `quoting`。这会产生脏业务数据。

状态：已处理。创建报价时已要求需求存在，且只允许 `published` / `quoting` 状态。

后续建议：

- 可选：同一创作者对同一需求只能存在一个有效报价。

---

## 中优先级问题

### 1. 基础 SQL 与增量 SQL 有重复职责

`creative_platform_tables.sql` 已包含审核字段，而 `creative_creator_audit_upgrade_20260425.sql` 又会 `alter table add column`。新库如果照全量顺序执行，增量脚本会因为字段已存在而失败。

已处理：本次把审核增量脚本改为先查 `information_schema.columns`，字段存在时跳过，因此新库和旧库都能按 `sql/README.md` 执行。后续最好继续把 SQL 分成 `base` 和 `upgrade` 两类目录，别全堆在一个文件夹里。

### 2. 订单来源字段已补齐

早期 `creative_custom_order` 只存买家、卖家、金额、状态，没有 `source_type`、`source_id`、`product_id` 或 `quote_id`。后续做商品现货订单、购物车、下架校验、售后追踪时会很难追溯来源。

状态：已处理。订单表已补 `source_type`、`source_id`、`source_name`、`quantity`、`pay_status`、`address_snapshot`，用于商品订单、购物车结算和模拟支付。

### 3. 前台公开查询接口已补齐

早期后台 Service 的数据权限偏“管理后台视角”，例如商品详情会按创作者归属校验。前台商品浏览、创作者主页、社区作品列表需要公开只读接口，不能直接复用后台管理接口。

状态：已处理。已新增 `/portal/*` 前台接口，区分后台 `/creative/*` 管理视角与前台门户只读/交互视角。

---

## 已整理内容

- `docs/collaboration.md`：改成“当前状态 -> 接手步骤 -> 路线 -> 任务清单 -> 历史记录 -> 未决问题”的顺序。
- `sql/README.md`：补充每个 SQL 文件的作用、新库执行顺序、旧库升级顺序和导入后检查语句。
- `README.md`：数据库初始化段落补充增量 SQL，并链接到 SQL 说明。
- `CreativeOrderServiceImpl` / `CreativeQuoteServiceImpl`：补订单动作角色限制和报价需求状态限制。
- `CreativePortalController` / `views/portal/*`：补前台门户、购物车、模拟支付、社区、收藏。
- `sql/order_payment_upgrade_20260428.sql`：补订单来源、支付状态、收货信息快照字段。
