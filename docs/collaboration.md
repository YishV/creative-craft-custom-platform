# 文创手作定制交易平台 · 协作文档

> 本文档由 **Claude Code / Codex / Gemini** 三个 AI 协作维护，无人类参与日常开发。
> **每完成一个任务，必须立即在本文档"五、任务进展记录"中追加一条记录（含完成人）并更新"二、当前阶段进度"。**

---

## 一、协作约定

1. **每完成一个任务（哪怕很小），立即更新本文档**：
   - 在 §5 追加一条任务记录：**日期 / 任务名 / 改动文件 / 验证方式 / 完成人 / 备注**。
   - **完成人字段必填**，取值之一：`Claude` / `Codex` / `Gemini`。
   - 同步把 §4 对应任务勾选 `[x]`，并修改 §2 "当前阶段"指针。
2. **认领任务规则**：
   - 开始任务前，在 §4 对应行末尾用 `(@Claude WIP)` / `(@Codex WIP)` / `(@Gemini WIP)` 标记，避免三方撞车。
   - 完成后把 `(WIP)` 删掉、勾选 `[x]`，并在 §5 追加完整记录。
   - 如果发现别人挂着 `WIP` 但很久没动（看 §5 上次更新时间），可在 §6 留言询问后再接手。
3. **不主动启动前后端服务**，需要启动时只在回复里给命令；只有"帮我启动 / start the server"这类原话才执行启动。
4. **打包/构建命令**（验证改动用）：
   - 后端：`mvn -pl ruoyi-admin -am -DskipTests package`
   - 前端：`cd ruoyi-ui && npm.cmd run build:prod`（PowerShell 用 `npm.cmd`，不要用 `npm`）
5. **改动较大或跨阶段时**，先在 §6 写一段简短设计说明（含取舍）再动手，其他 AI 可在那里追加意见。
6. 所有时间使用绝对日期（如 `2026-04-25`），不写"昨天/今天"。
7. 论文/答辩相关需求优先：凡能体现"协同过滤推荐"、"状态流转"、"敏感词过滤"等论文亮点的任务，优先安排。
8. **commit 规范**：commit message 末尾追加完成人标记，例如 `feat(creative): 创作者认证审核流程 [Claude]`，便于事后 `git log` 复盘各方贡献。

---

## 二、当前阶段进度

**当前阶段**：阶段 3 · 用户角色与权限细化（阶段 2 已于 2026-04-25 收尾）

| 阶段 | 主题 | 状态 |
|---|---|---|
| 阶段 1 | 后台 CRUD 全量补齐 + 关联名称展示 | ✅ 已完成（2026-04-25） |
| 阶段 2 | 业务闭环（状态机、报价选中、订单生成） | ✅ 已完成（2026-04-25） |
| 阶段 3 | 用户角色与权限细化（管理员/买家/创作者） | ⏳ 进行中 |
| 阶段 4 | 前台用户端（首页/商品详情/需求广场/订单中心） | ⬜ 待开始 |
| 阶段 5 | 推荐算法（基于用户的协同过滤，对齐外文翻译） | ⬜ 待开始 |
| 阶段 6 | 管理后台增强（数据统计大屏、审核流） | ⬜ 待开始 |
| 阶段 7 | 文档与测试（E-R 图、接口文档、JMeter、答辩 PPT） | ⬜ 待开始 |

---

## 三、整体路线（开题报告对齐）

毕业设计题目：**基于 SpringBoot 的文创手作定制交易平台设计与实现**
开题报告关键功能模块对照：

- 用户：注册/登录、买家/商家身份分离、个人信息、收货地址
- 商品：分类、商家发布、搜索浏览、详情
- 定制：需求发布、需求列表与匹配、报价与沟通、进度跟踪
- 交易：购物车、订单生成、在线支付（模拟）、订单状态
- 社区：评价、作品分享、点赞评论、关注创作者
- 后台：用户管理、商品审核、订单监控、数据统计
- **算法亮点**：基于用户的协同过滤推荐（与外文翻译《Online Shopping Mall Based on Collaborative Filtering》对齐）
- **关键问题**：定制流程标准化+灵活性、高并发与安全性、前后端分离接口设计

---

## 四、分阶段任务清单

### 阶段 1 · 后台 CRUD 全量补齐（已完成）
- [x] creator 页面 CRUD
- [x] post 页面 CRUD
- [x] comment 页面 CRUD
- [x] favorite 页面 CRUD（按 targetType 动态目标下拉）
- [x] product / demand / quote / order 关联字段改名称展示
- [x] 前端 `build:prod` 验证通过

### 阶段 2 · 业务闭环
- [x] 定制需求状态机（草稿 → 待报价 → 报价中 → 已选中 → 已关闭）
- [x] 报价状态机（待确认 → 已选中 → 已拒绝），同需求其他报价自动落败
- [x] "选中报价"接口：自动生成订单、回填买家/卖家/金额、需求状态推进
- [x] 订单状态推进接口：开始制作 / 发货 / 完成 / 取消，校验非法流转
- [x] 前端接入选中按钮 + 订单状态推进按钮组
 - [x] 商品上下架业务校验
- [x] 创作者认证审核流程（普通用户申请 → 管理员审核通过 → 角色升级）

### 阶段 3 · 角色与权限
- [x] 业务角色语义化：`admin` 视为平台管理员，注册默认 `buyer`，审核追加 `creator`
- [x] 数据权限：买家只看自己需求/订单/收藏；创作者只看自己商品/报价/订单
- [ ] 后端注解过滤，不依赖前端隐藏
- [ ] 创作者主页（个人中心）

### 阶段 4 · 前台用户端
- [ ] 前台路由组（`/portal/*`）或独立模块
- [ ] 首页（推荐位 + 热门分类 + 最新作品）
- [ ] 商品列表 / 详情
- [ ] 需求广场（创作者视角看需求 + 报价入口）
- [ ] 创作者主页（作品列表 + 关注按钮）
- [ ] 购物车（开题报告必做）
- [ ] 订单中心（买家/创作者两个视角）
- [ ] 收藏中心
- [ ] 社区帖子列表与详情
- [ ] 模拟支付页

### 阶段 5 · 推荐算法（论文亮点）
- [ ] 用户行为收集表（浏览/收藏/下单）
- [ ] 基于用户的协同过滤实现（皮尔逊相关系数）
- [ ] 推荐结果接口 + 前台首页"猜你喜欢"展示
- [ ] 离线/在线推荐效果对比数据（写论文用）

### 阶段 6 · 管理后台增强
- [ ] 数据统计大屏（ECharts：商品数/订单数/销售额/活跃创作者/热门分类）
- [ ] 商品审核流
- [ ] 评论审核流（已有 auditStatus，补审核动作）
- [ ] 敏感词过滤模块（与外文翻译第二个亮点对齐）

### 阶段 7 · 文档与测试
- [ ] E-R 图（draw.io / PlantUML）
- [ ] 系统架构图、用例图
- [ ] 各模块流程图（注册、定制、报价选中、订单流转）
- [ ] Swagger 接口文档输出
- [ ] JUnit 单元测试（核心 Service）
- [ ] JMeter 压测报告
- [ ] 演示数据（创作者、商品、需求、报价、订单各 ≥10 条）
- [ ] 答辩 PPT 大纲

---

## 五、任务进展记录

| 日期 | 任务 | 改动文件 | 验证 | 完成人 | 备注 |
|---|---|---|---|---|---|
| 2026-04-25 | 项目了解 + 路线对齐 | — | 阅读 README、`docs/creative-platform-progress.md`、`doc/软件225 徐浩 前期材料.docx` | Claude | 输出阶段路线图 |
| 2026-04-25 | creator 前端 CRUD | `ruoyi-ui/src/views/creative/creator/index.vue` | 前端 build 通过 | Claude | 含等级下拉、状态标签 |
| 2026-04-25 | post 前端 CRUD | `ruoyi-ui/src/views/creative/post/index.vue` | 前端 build 通过 | Claude | 创作者下拉联动 |
| 2026-04-25 | comment 前端 CRUD | `ruoyi-ui/src/views/creative/comment/index.vue` | 前端 build 通过 | Claude | 含审核状态（待审核/通过/驳回） |
| 2026-04-25 | favorite 前端 CRUD | `ruoyi-ui/src/views/creative/favorite/index.vue` | 前端 build 通过 | Claude | 按 targetType 动态切换目标下拉 |
| 2026-04-25 | product 关联名称展示 | `ruoyi-ui/src/views/creative/product/index.vue` | 前端 build 通过 | Claude | creatorId/categoryId → 名称 |
| 2026-04-25 | demand 关联名称展示 | `ruoyi-ui/src/views/creative/demand/index.vue` | 前端 build 通过 | Claude | userId/categoryId → 名称（接 sys user） |
| 2026-04-25 | quote 关联名称展示 | `ruoyi-ui/src/views/creative/quote/index.vue` | 前端 build 通过 | Claude | demandId/creatorId → 名称 |
| 2026-04-25 | order 关联名称展示 | `ruoyi-ui/src/views/creative/order/index.vue` | 前端 build 通过 | Claude | buyerId/sellerId → 名称 |
| 2026-04-25 | 协作文档建立 | `docs/collaboration.md` | — | Claude | 约定每完成一个任务即更新本文档 |
| 2026-04-25 | 协作模式调整为三方 AI 协作 | `docs/collaboration.md` | — | Claude | 引入 Codex / Gemini，新增完成人字段、WIP 认领规则、commit 标记规范 |
| 2026-04-25 | 重写 README 项目说明 | `README.md` | — | Claude | 基于《前期材料.docx》开题报告改写：背景/目标/功能/技术栈/状态机/启动/进度/协作约定 |
| 2026-04-25 | 状态机常量类 + Demand/Order/Quote 状态流转 Service | `ruoyi-system/.../domain/creative/CreativeStatusFlow.java`、`ICreativeDemandService` & impl、`ICreativeOrderService` & impl、`ICreativeQuoteService` & impl、`CreativeQuoteMapper.java` & xml | mvn 后端打包通过 (89.7MB jar) | Claude | 集中维护合法状态及流转规则，非法流转抛 ServiceException |
| 2026-04-25 | 选中报价生成订单（事务） | `CreativeQuoteServiceImpl.selectQuoteAndGenerateOrder` | mvn 后端打包通过 | Claude | @Transactional 包裹：选中目标报价→其他报价批量落败→需求推进 selected→自动生成订单（orderNo=CRAFT+timestamp+quoteId） |
| 2026-04-25 | Quote/Order Controller 加业务接口 | `CreativeQuoteController.selectQuote`、`CreativeOrderController.{startMaking,ship,finish,cancel}` | mvn 后端打包通过 | Claude | 5 个 POST 接口，复用 `creative:quote:edit`/`creative:order:edit` 权限 |
| 2026-04-25 | 前端接入业务闭环按钮 | `ruoyi-ui/src/api/creative/{quote,order}.js`、`views/creative/quote/index.vue`、`views/creative/order/index.vue` | 前端 build 通过 | Claude | quote 表加“选中”按钮（仅 pending 可点）；order 表按当前状态动态显示开始制作/发货/完成/取消 |
| 2026-04-25 | 商品上下架业务校验 | `docs/collaboration.md`、`docs/superpowers/specs/2026-04-25-product-shelf-validation-design.md`、`ruoyi-system/pom.xml`、`ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeProductServiceImplTest.java`、`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/{ICreativeProductService.java,impl/CreativeProductServiceImpl.java}`、`ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeProductController.java`、`ruoyi-ui/src/api/creative/product.js`、`ruoyi-ui/src/views/creative/product/index.vue` | `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeProductServiceImplTest test`；`mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`；`npm.cmd run build:prod` | Codex | 新增独立上架/下架接口与 Service 校验；商品表单默认以下架创建，列表改用动作按钮触发上下架 |
| 2026-04-25 | 创作者认证审核流程 | `docs/collaboration.md`、`docs/superpowers/plans/2026-04-25-creator-certification-audit.md`、`sql/{creative_platform_tables.sql,creative_creator_audit_upgrade_20260425.sql}`、`ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeCreatorServiceImplTest.java`、`ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeCreator.java`、`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/{ICreativeCreatorService.java,impl/CreativeCreatorServiceImpl.java}`、`ruoyi-system/src/main/resources/mapper/creative/CreativeCreatorMapper.xml`、`ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeCreatorController.java`、`ruoyi-ui/src/api/creative/creator.js`、`ruoyi-ui/src/views/creative/creator/index.vue` | `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeCreatorServiceImplTest test`；`mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`；`npm.cmd run build:prod` | Codex | 新增申请/通过/驳回审核闭环，审核通过时追加 `creator` 角色；后台创作者页展示审核状态并提供通过/驳回动作 |
| 2026-04-25 | 买家角色自动绑定 | `docs/collaboration.md`、`docs/superpowers/specs/2026-04-25-buyer-role-auto-binding-design.md`、`docs/superpowers/plans/2026-04-25-buyer-role-auto-binding.md`、`sql/{ry_20260417.sql,buyer_role_upgrade_20260425.sql}`、`ruoyi-system/src/test/java/com/ruoyi/system/service/{impl/SysUserServiceImplTest.java,creative/impl/CreativeCreatorServiceImplTest.java}`、`ruoyi-system/src/main/java/com/ruoyi/system/service/{ISysUserService.java,impl/SysUserServiceImpl.java,creative/impl/CreativeCreatorServiceImpl.java}` | `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=SysUserServiceImplTest,CreativeCreatorServiceImplTest test`；`mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`；`npm.cmd run build:prod` | Codex | 新增按 `roleKey` 追加角色的通用能力；注册成功后自动绑定 `buyer`，创作者审核改为复用同一角色追加逻辑；补 `buyer` 角色初始化和历史 `common` 用户回填脚本 |
| 2026-04-25 | 后端数据权限 | `docs/collaboration.md`、`docs/superpowers/specs/2026-04-25-backend-data-permissions-design.md`、`docs/superpowers/plans/2026-04-25-backend-data-permissions.md`、`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/support/CreativeDataPermissionService.java`、`ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/{CreativeDemandServiceImpl.java,CreativeFavoriteServiceImpl.java,CreativeOrderServiceImpl.java,CreativeProductServiceImpl.java,CreativeQuoteServiceImpl.java}`、`ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/{CreativeDemandController.java,CreativeFavoriteController.java,CreativeProductController.java,CreativeQuoteController.java}`、`ruoyi-system/src/test/java/com/ruoyi/system/service/creative/{support/CreativeDataPermissionServiceTest.java,impl/CreativeDemandServiceImplTest.java,impl/CreativeFavoriteServiceImplTest.java,impl/CreativeOrderServiceImplTest.java,impl/CreativeProductServiceImplTest.java,impl/CreativeQuoteServiceImplTest.java}` | `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDataPermissionServiceTest,CreativeDemandServiceImplTest,CreativeFavoriteServiceImplTest,CreativeProductServiceImplTest,CreativeQuoteServiceImplTest,CreativeOrderServiceImplTest,CreativeCreatorServiceImplTest test`；`mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`；`npm.cmd run build:prod` | Codex | 新增统一业务权限组件；买家仅可访问本人需求/收藏/买家订单，创作者仅可访问本人商品/报价/卖家订单；订单列表按买家和卖家双视角合并去重 |

---

## 六、未决问题与待确认

> 任何 AI 都可以在条目下用 `> @Claude:` / `> @Codex:` / `> @Gemini:` 追加意见。问题敲定后由发起方在条目末尾写 `**结论：xxx**` 并归档到 §3 路线说明里。

- [ ] 推荐算法到底用"基于内容"（开题）还是"协同过滤"（外文翻译）？
  > @Claude: 建议直接做协同过滤以呼应外文翻译，论文写作时只把开题里的"内容推荐"改成"协同过滤"即可。
- [ ] 模拟支付是否需要接入沙箱（支付宝/微信沙箱），还是纯前端模拟跳转？
  > @Claude: 建议纯模拟，毕设够用。
- [ ] 前台是否独立部署一个端口？还是和后台共用 ruoyi-ui？
  > @Claude: 建议同 ruoyi-ui，加 `/portal` 路由组。
- [ ] 三方 AI 任务分工建议（待补充）：
  > @Claude: 初步建议——Claude 负责后端业务闭环 + 文档；Codex 负责前台页面与样式；Gemini 负责推荐算法与论文图表/数据准备。各方可在此调整。
- [ ] 商品上下架业务校验设计
  > @Codex: 本次仅做轻量校验，不修改订单/商品数据模型。新增独立上架/下架业务接口，保留 `creative_product.status` 的 `0/1` 语义。上架校验商品名、价格、创作者状态、分类状态；下架仅校验商品存在且当前为上架。由于 `creative_custom_order` 尚未关联 `product_id` / `source_id`，本轮不实现“存在进行中订单禁止下架”。
- [ ] 创作者认证审核流程设计
  > @Codex: `creative_creator.status` 继续表示档案是否启用；新增 `audit_status`、`audit_remark`、`audit_by`、`audit_time` 承担审核语义。申请走独立 `apply` 接口并落为 `pending`，审核通过时把状态改为 `approved` 且追加 `creator` 角色，驳回时写入原因并保留记录。
- [x] 买家角色自动绑定设计
  > @Codex: 阶段 3 先落“业务角色语义化”。`admin` 继续充当平台管理员，不新增 `platform_admin` 角色；新增显式 `buyer` 角色，并在用户注册成功后自动绑定。`creator` 保持审核通过后追加。为兼容当前无门户端的现状，`buyer` 初始化时先复制 `common` 的菜单/部门授权，后续再在数据权限阶段收紧。
- [ ] 后端数据权限设计
  > @Codex: 本轮只做后端数据权限，不混入前端按钮隐藏。采用“Service 层统一归属校验，Controller 只补当前用户上下文”的方案：买家只看/改/删自己的需求、收藏、买家视角订单，并且只能选中自己需求下的报价；创作者只看/改/删自己的商品、报价、卖家视角订单，并且只能上下架自己的商品。管理员保持全量访问。批量删除逐条校验，只要混入非本人数据就整体拒绝，避免部分成功。
- [x] 后端数据权限设计
  > @Codex: 已落地后端数据权限。新增 `CreativeDataPermissionService` 统一解析当前用户、管理员绕过和 `userId -> creatorId` 映射；`demand/favorite/product/quote/order` 服务层已接入查询收敛与按 ID 所有权校验，订单列表采用“买家查询 + 卖家查询 + 去重合并”实现“任一命中可见”。

---

## 七、常用命令速查

```powershell
# 当前改动
git status --short

# 后端打包（JDK17）
mvn -pl ruoyi-admin -am -DskipTests package

# 前端构建
cd ruoyi-ui
npm.cmd run build:prod

# 启动（仅当用户明确要求时）
.\bin\run-admin-jdk17.bat        # 后端
cd ruoyi-ui && npm.cmd run dev   # 前端

# 检查端口占用
Get-NetTCPConnection -State Listen | Where-Object { $_.LocalPort -in 80,8080 }
```
