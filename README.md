# 文创手作定制交易平台

> 毕业设计题目：**基于 SpringBoot 的文创手作定制交易平台设计与实现**
> 学生：徐浩（软件 225，学号 2200770527）｜指导老师：游静（副教授）｜2026 届
> 框架基础：[RuoYi-Vue v3.9.2](https://gitee.com/y_project/RuoYi-Vue)（Spring Boot + Vue 前后端分离）

本项目由 **Claude / Codex / Gemini** 三方 AI 协作开发，完整协作过程与每日进度见 [`docs/collaboration.md`](docs/collaboration.md)。

---

## 一、课题背景与意义

随着文化创意产业的蓬勃发展和消费升级，**个性化、定制化的文创手作产品**日益受到年轻消费者青睐。然而：

- 传统电商平台以标准化商品为主，缺乏对**定制化交易流程**的有效支持；
- 手工艺人与消费者之间**沟通成本高**、**信任机制不健全**、**交易过程不透明**；
- 现有定制类平台普遍**缺少社区氛围**与个性化推荐能力。

本平台面向上述痛点，构建一个集 **作品展示、定制需求发布、报价沟通、订单跟踪、社区互动、个性化推荐** 于一体的文创手作交易平台，促进文创产业数字化转型。

---

## 二、研究目标与功能范围

### 2.1 总体目标

设计并实现一个支持 **用户注册登录、商品浏览、定制需求发布、在线沟通、订单管理、模拟支付、社区互动** 的 Web 应用：

- 实现 **手工艺人（创作者/商家）** 与 **普通用户（买家）** 端的功能分离与差异化界面；
- 引入 **协同过滤推荐算法**（基于用户的协同过滤 + 皮尔逊相关系数），按浏览/收藏/下单行为推荐相关手作商品或创作者；
- 设计合理的 **定制交易流程状态机**，保障买卖双方权益、降低交易纠纷；
- 提供完善的 **后台管理模块**，支持平台运营方对用户、商品、订单、评论进行有效管理。

### 2.2 功能模块

| 模块 | 主要功能 |
|---|---|
| **用户模块** | 注册、登录、身份选择（买家/创作者）、个人信息、收货地址 |
| **商品模块** | 商品分类、商品发布（创作者）、搜索浏览、商品详情 |
| **定制模块** | 定制需求发布（买家）、需求广场与匹配、报价与沟通、定制进度跟踪 |
| **交易模块** | 购物车、订单生成、模拟在线支付、订单状态流转 |
| **社区模块** | 用户评价、作品分享、点赞评论、关注创作者 |
| **后台管理** | 用户管理、商品管理、订单监控、评论管理、数据统计大屏 |
| **算法模块** | 基于用户的协同过滤推荐；敏感词过滤作为可选增强 |

### 2.3 开题需求对照

下面按开题报告截图中的功能逐项对应，方便答辩或后续接手时快速判断系统完成度。

| 开题要求 | 当前实现 | 状态 |
|---|---|---|
| 用户注册、登录 | 登录页已显示“立即注册”；注册页支持买家/创作者身份选择；SQL 默认开启自助注册 | 已完成 |
| 身份选择（买家/商家） | 注册时选择买家或创作者；创作者注册后需提交认证申请，审核通过后追加 `creator` 角色 | 已完成 |
| 个人信息管理 | 复用系统个人中心；创作者有个人档案与统计页 | 基础完成 |
| 收货地址管理 | 结算页填写收货人、手机号、地址，并写入订单收货快照 | 演示版 |
| 商品分类 | `creative_category` 管理分类 | 已完成 |
| 商品发布（商家/创作者） | 创作者在后台发布商品，支持上下架校验 | 已完成 |
| 商品搜索与浏览 | `/portal/products` 提供前台商品浏览、搜索、详情查看 | 基础完成 |
| 定制需求发布（买家） | `/portal/demands` 可发布需求，后台也有需求管理 | 基础完成 |
| 需求列表与匹配 | 需求广场展示开放需求，创作者可查看并报价 | 基础完成 |
| 需求报价与沟通 | 已有报价流程；在线沟通目前用需求说明、报价说明承载，实时聊天未做 | 演示版 |
| 定制进度跟踪 | 需求、报价、订单均有状态机，订单支持制作、发货、完成、取消 | 已完成 |
| 购物车 | `/portal/cart` 支持加入商品、数量修改、删除、结算；购物车数据保存在浏览器本地 | 演示版 |
| 订单生成 | 选中报价自动生成订单；订单模块可管理状态 | 已完成 |
| 在线支付 | `/portal/payment` 提供模拟支付，支付成功后订单 `pay_status` 更新为 `paid` | 演示版 |
| 用户评价、作品分享 | `/portal/community` 展示作品列表，`/portal/post/:id` 支持详情与评论 | 基础完成 |
| 点赞评论、关注创作者 | 支持商品/作品收藏、关注创作者、收藏中心；点赞计数暂未单独实现 | 基础完成 |
| 后台管理 | 用户、商品、需求、报价、订单、评论、作品等后台管理已具备 | 已完成 |
| 数据统计 | 暂未做独立统计大屏 | 待完善 |
| 推荐算法 | 计划实现用户行为表 + 协同过滤 + 首页推荐 | 待完善 |

优先级建议：交易演示闭环和社区前台已具备基础版本，下一步补 **推荐算法 + 数据统计大屏**，用于提升论文和演示亮点。

### 2.4 待解决的关键问题

1. **定制流程的标准化与灵活性**：把非标的定制需求转化为可跟踪的订单状态机，同时保留沟通灵活性。
2. **买家端与创作者端的差异化体验**：买家更关注浏览、下单、发布需求；创作者更关注商品发布、报价、制作进度。
3. **前后端分离架构**下的接口设计与演示流畅度：优先保证答辩演示能从注册、浏览、需求、报价、订单顺畅走通。

---

## 三、技术选型

| 层 | 技术栈 |
|---|---|
| **后端框架** | Spring Boot 4.x、Spring MVC、Spring Security、JWT |
| **持久层** | MyBatis、MySQL 8.0 |
| **缓存** | Redis（本地未启动时使用内存兜底，已在 `RedisCache` 中实现） |
| **前端框架** | Vue 2、Vue Router 3、Vuex、Axios |
| **UI 组件库** | Element UI |
| **构建工具** | Maven（多模块）、Vue CLI |
| **服务器** | Tomcat（内置） |
| **测试与压测** | JUnit、JMeter、Postman |
| **版本管理** | Git |

### 系统架构（四层）

```
┌─────────────────────────────────────────┐
│  前端 (Vue + Element UI)                 │
└─────────────────┬───────────────────────┘
                  │ HTTP / Axios
┌─────────────────▼───────────────────────┐
│  过滤器/拦截器层 (JWT、权限、敏感词过滤)   │
├─────────────────────────────────────────┤
│  Controller 层 (REST API、参数校验)      │
├─────────────────────────────────────────┤
│  Service 层 (业务逻辑、状态机、推荐算法)  │
├─────────────────────────────────────────┤
│  数据访问层 (MyBatis Mapper)             │
└─────────────────┬───────────────────────┘
                  │ JDBC
              ┌───▼───┐
              │ MySQL │
              └───────┘
```

---

## 四、模块结构

```
creative-craft-custom-platform/
├── ruoyi-admin/        # 启动模块 + 业务 Controller
│   └── src/main/java/com/ruoyi/web/controller/creative/
│       ├── CreativeCategoryController.java   # 文创分类
│       ├── CreativeCreatorController.java    # 创作者档案
│       ├── CreativeProductController.java    # 手作商品
│       ├── CreativeDemandController.java     # 定制需求
│       ├── CreativeQuoteController.java      # 定制报价
│       ├── CreativeOrderController.java      # 定制订单
│       ├── CreativePostController.java       # 社区作品
│       ├── CreativeCommentController.java    # 评论
│       ├── CreativeFavoriteController.java   # 收藏
│       └── CreativePortalController.java     # 前台门户商品/需求接口
├── ruoyi-system/       # 业务 Domain / Mapper / Service
│   └── src/main/java/com/ruoyi/system/
│       ├── domain/creative/
│       ├── mapper/creative/
│       └── service/creative/
├── ruoyi-framework/    # 框架配置（拦截器、AOP）
├── ruoyi-common/       # 通用工具（含本地 RedisCache 兜底）
├── ruoyi-quartz/       # 定时任务
├── ruoyi-generator/    # 代码生成
├── ruoyi-ui/           # 前端工程
│   └── src/
│       ├── api/creative/        # 后台业务 API + 前台门户 API
│       ├── views/creative/      # 后台业务页面
│       └── views/portal/        # 前台首页、商品、需求、购物车、支付、社区、收藏页面
├── sql/
│   ├── ry_20260417.sql              # 若依基础 SQL
│   ├── creative_platform_tables.sql # 文创业务表
│   ├── creative_platform_menu.sql   # 业务菜单 + 按钮权限
│   ├── creative_creator_audit_upgrade_20260425.sql # 创作者审核字段与角色
│   ├── buyer_role_upgrade_20260425.sql             # 买家角色初始化
│   ├── creator_me_menu_20260426.sql                # 创作者个人中心菜单
│   ├── register_enable_20260427.sql                # 开启自助注册
│   ├── order_payment_upgrade_20260428.sql          # 商品订单/模拟支付字段
│   ├── hide_ruoyi_brand_upgrade_20260428.sql       # 隐藏默认若依外链与公告
│   └── quartz.sql
├── doc/
│   ├── 软件225 徐浩 前期材料.docx     # 任务书 / 开题报告 / 外文翻译
│   └── 若依环境使用手册.docx
├── docs/
│   └── collaboration.md             # 三方 AI 协作文档（路线、进度、约定）
├── bin/
│   └── run-admin-jdk17.bat          # JDK 17 启动脚本
└── pom.xml
```

---

## 五、业务实体与定制流程

### 5.1 核心实体

| 实体 | 作用 |
|---|---|
| `creative_category` | 文创分类（如手工皮具、刺绣、陶艺） |
| `creative_creator` | 创作者档案（绑定系统用户、店铺名、等级） |
| `creative_product` | 现货/定制商品 |
| `creative_demand` | 买家发布的定制需求 |
| `creative_quote` | 创作者对需求的报价 |
| `creative_custom_order` | 订单（来源：直接购买商品 / 选中报价生成），包含支付状态、购买数量和收货信息快照 |
| `creative_post` | 社区作品分享 |
| `creative_comment` | 评论（含审核状态） |
| `creative_favorite` | 收藏（支持商品/创作者/作品三种 targetType） |

### 5.2 定制交易状态机

```
[需求 demand]
  草稿 ──发布──▶ 待报价 ──收到报价──▶ 报价中 ──选中报价──▶ 已选中
                                                              │
                                                              ▼
                                                         [自动生成订单]
                                                              │
[订单 order]                                                   │
  已创建 ──开始制作──▶ 制作中 ──发货──▶ 已发货 ──确认收货──▶ 已完成
                                                    │
                                                    └──取消──▶ 已取消

[报价 quote]
  待确认 ──被买家选中──▶ 已选中
        └──其他报价自动──▶ 已拒绝
```

### 5.3 算法亮点（论文）

1. **基于用户的协同过滤推荐**：使用皮尔逊相关系数计算用户相似度，找到目标用户的"最近邻"，按其偏好预测推荐商品 / 创作者（与外文翻译《Online Shopping Mall Based on Collaborative Filtering》对齐）。
2. **行为数据驱动推荐**：后续通过浏览、收藏、下单等行为形成用户偏好矩阵，用于首页“猜你喜欢”和论文实验说明。
3. **动态敏感词过滤机制（可选增强）**：如时间允许，可在评论提交前做敏感词匹配与替换，用于补充内容治理说明。

---

## 六、环境要求

| 项 | 版本 |
|---|---|
| 操作系统 | Windows 10 / 11 |
| JDK | **17+** |
| Maven | 3.6+ |
| Node.js | 16+（推荐 18 LTS） |
| MySQL | 8.0 |
| Redis | 可选（不安装时自动走内存兜底） |
| 浏览器 | Chrome / Edge |

---

## 七、本地启动

### 7.1 数据库初始化

更详细的 SQL 作用和新库/旧库执行顺序见 [`sql/README.md`](sql/README.md)。

```sql
-- MySQL 中按顺序导入
source sql/ry_20260417.sql;
source sql/quartz.sql;
source sql/creative_platform_tables.sql;
source sql/creative_platform_menu.sql;
source sql/creative_creator_audit_upgrade_20260425.sql;
source sql/buyer_role_upgrade_20260425.sql;
source sql/creator_me_menu_20260426.sql;
source sql/register_enable_20260427.sql;
source sql/order_payment_upgrade_20260428.sql;
source sql/hide_ruoyi_brand_upgrade_20260428.sql;
```

### 7.2 启动后端（JDK 17）

```powershell
# 方式 1：使用启动脚本（自动选 JDK 17）
.\bin\run-admin-jdk17.bat

# 方式 2：手动 mvn 打包后运行
mvn -pl ruoyi-admin -am -DskipTests package
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

后端地址：`http://localhost:8080`

### 7.3 启动前端

```powershell
cd ruoyi-ui
npm.cmd install      # 首次
npm.cmd run dev
```

前端地址：`http://localhost/`
（PowerShell 必须用 `npm.cmd`，不要用 `npm`，否则会报 `npm.ps1 cannot be loaded`）

### 7.4 默认账号

```
admin / admin123
```

---

## 八、构建与验证

```powershell
# 后端打包验证
mvn -pl ruoyi-admin -am -DskipTests package

# 前端生产构建验证
cd ruoyi-ui
npm.cmd run build:prod
```

---

## 九、当前进度

详见 [`docs/collaboration.md`](docs/collaboration.md)。

| 阶段 | 状态 |
|---|---|
| 阶段 1：后台 9 模块 CRUD + 关联名称展示 | ✅ 已完成 |
| 阶段 2：业务闭环（状态机、报价选中生成订单） | ✅ 已完成 |
| 阶段 3：角色权限细化（管理员/买家/创作者） | ✅ 已完成 |
| 阶段 4：前台用户端（首页/商品详情/需求广场/订单中心/购物车/社区） | ✅ 基础完成 |
| 阶段 5：协同过滤推荐（论文亮点） | ⬜ 待开始 |
| 阶段 6：管理后台增强（数据统计大屏、审核流、可选敏感词过滤） | ⬜ 待开始 |
| 阶段 7：文档与测试（E-R 图、Swagger、JUnit、JMeter、答辩 PPT） | ⬜ 待开始 |

---

## 十、协作说明

本项目由 **Claude / Codex / Gemini** 三方 AI 协作完成，无人类编码参与。所有任务的认领、改动、验证、完成人都记录在 [`docs/collaboration.md`](docs/collaboration.md) 中。

**核心约定**：
- 每完成一个任务（哪怕很小），立即在协作文档 §5 追加记录并标注完成人
- 开始任务前在协作文档 §4 对应行末打 `(@Claude WIP)` / `(@Codex WIP)` / `(@Gemini WIP)` 避免撞车
- commit message 末尾追加完成人标记，如 `feat(creative): xxx [Claude]`
- 不主动启动前后端服务，需要启动时只给命令

---

## 十一、参考文献

- 耿庆阳. 基于 Spring Boot 和 Vue 的电子商务商城的设计与实现 [D]. 西安石油大学, 2020.
- 孙宏攀. 基于 SpringBoot 和 Vue 的有为通信社区的设计与实现 [D]. 重庆大学, 2022.
- 付鹤岗, 王祝伟. 基于项目的协同过滤推荐系统的改进 [J]. 重庆理工大学学报(自然科学版), 2010, 24(09): 69-74.
- 李晨. 基于 Spring Boot 的电子商务商城的设计与实现 [D]. 哈尔滨工业大学, 2020.
- Suryotrisongko H, Jayanto D P, Tjahyanto A. Design and Development of Backend Application for Public Complaint Systems Using Microservice Spring Boot [J]. Procedia Computer Science, 2017, 124: 736-743.
- 王福强. Spring Boot 揭秘：快速构建微服务体系 [M]. 北京: 机械工业出版社, 2016.
- 完整文献列表见 `doc/软件225 徐浩 前期材料.docx`。

---

## License

本项目基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) (MIT License) 二次开发，毕业设计用途。
