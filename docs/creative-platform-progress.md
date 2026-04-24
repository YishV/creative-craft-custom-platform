# 文创手作定制交易平台进度交接文档

更新时间：2026-04-24

项目路径：`D:\workspace\sihuo\RuoYi-Vue-master`

## 1. 项目定位

本项目基于 `RuoYi-Vue-master` 前后端分离框架搭建，毕业设计题目为：

`基于SpringBoot的文创手作定制交易平台设计与实现`

当前目标是先完成后台管理端基础框架和核心业务 CRUD，后续再逐步补全前台展示、交易流程、权限细化和业务闭环。

## 2. 当前已完成

### 2.1 数据库与菜单 SQL

已新增以下 SQL 文件：

- `sql/creative_platform_tables.sql`
- `sql/creative_platform_menu.sql`

作用：

- 创建文创平台业务表。
- 创建后台菜单和按钮权限。
- 当前菜单模块包含文创分类、创作者、商品、定制需求、定制报价、定制订单、社区帖子、评论、收藏。

注意：

- 需要先导入若依原始 SQL，再导入这两个业务 SQL。
- 如果菜单不显示，检查当前登录用户是否拥有对应菜单权限。

### 2.2 后端业务骨架

后端已建立 `creative` 业务包，包含 9 个模块的 Controller、Domain、Mapper、Service、ServiceImpl、Mapper XML。

已存在模块：

- 文创分类：`CreativeCategory`
- 创作者档案：`CreativeCreator`
- 手作商品：`CreativeProduct`
- 定制需求：`CreativeDemand`
- 定制报价：`CreativeQuote`
- 定制订单：`CreativeOrder`
- 社区帖子：`CreativePost`
- 评论：`CreativeComment`
- 收藏：`CreativeFavorite`

主要目录：

- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/creative`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl`
- `ruoyi-system/src/main/resources/mapper/creative`

当前后端状态：

- 基础增删改查接口已生成。
- 已通过 Maven 打包验证。
- 尚未做复杂业务校验、状态流转校验、订单生成规则、库存或支付逻辑。

### 2.3 前端 API

已新增 9 个前端接口文件：

- `ruoyi-ui/src/api/creative/category.js`
- `ruoyi-ui/src/api/creative/creator.js`
- `ruoyi-ui/src/api/creative/product.js`
- `ruoyi-ui/src/api/creative/demand.js`
- `ruoyi-ui/src/api/creative/quote.js`
- `ruoyi-ui/src/api/creative/order.js`
- `ruoyi-ui/src/api/creative/post.js`
- `ruoyi-ui/src/api/creative/comment.js`
- `ruoyi-ui/src/api/creative/favorite.js`

这些文件已按若依常规方式封装 `list/get/add/update/del` 请求。

### 2.4 已完成 CRUD 页面

以下 5 个页面已经从占位页改成可用的基础 CRUD 页面：

- `ruoyi-ui/src/views/creative/category/index.vue`
- `ruoyi-ui/src/views/creative/product/index.vue`
- `ruoyi-ui/src/views/creative/demand/index.vue`
- `ruoyi-ui/src/views/creative/quote/index.vue`
- `ruoyi-ui/src/views/creative/order/index.vue`

页面能力：

- 查询
- 分页
- 新增
- 修改
- 删除
- 表单校验
- 状态标签显示
- 分类下拉基础联动，商品和需求页面会读取文创分类数据

### 2.5 Redis 本地兜底

因为本机未启动 Redis，若依原项目启动时会在缓存加载处失败。当前已在 `RedisCache` 中加入本地内存兜底：

- `ruoyi-common/src/main/java/com/ruoyi/common/core/redis/RedisCache.java`

同时修复了字典缓存兼容问题：

- `ruoyi-common/src/main/java/com/ruoyi/common/utils/DictUtils.java`

修复原因：

- 本地兜底缓存返回 `ArrayList`。
- 若依原字典工具按 `JSONArray` 处理。
- 进入用户管理加载 `sys_user_sex` 字典时会报 `ArrayList cannot be cast to JSONArray`。
- 当前已兼容 `JSONArray` 和 `List` 两种缓存结果。

说明：

- 这是为了毕业设计本地开发降低环境依赖。
- 正式部署时仍建议安装并启用 Redis。

### 2.6 启动脚本

已新增 JDK17 后端启动脚本：

- `bin/run-admin-jdk17.bat`

原因：

- 当前命令行默认 Java 可能是 Java 8。
- 该项目当前构建目标使用 Java 17。
- 直接用 Java 8 启动会失败。

## 3. 已验证结果

已执行过以下验证：

```powershell
mvn -pl ruoyi-admin -am -DskipTests package
```

结果：

- 后端打包成功。
- 测试按命令跳过。

已执行过以下验证：

```powershell
npm.cmd run build:prod
```

结果：

- 前端生产构建成功。
- 存在若依自带的大包体积 warning，不影响运行。

接口验证：

- `http://localhost:8080/captchaImage` 返回 200。

当前服务状态：

- 按用户要求，前端和后端服务已经停止。
- 下次不要自动启动服务，除非用户明确要求。

## 4. 下次启动方式

### 4.1 启动后端

推荐使用：

```powershell
cd D:\workspace\sihuo\RuoYi-Vue-master
.\bin\run-admin-jdk17.bat
```

后端地址：

```text
http://localhost:8080
```

### 4.2 启动前端

```powershell
cd D:\workspace\sihuo\RuoYi-Vue-master\ruoyi-ui
npm.cmd run dev
```

前端地址：

```text
http://localhost/
```

如果 PowerShell 报 `npm.ps1 cannot be loaded because running scripts is disabled`，使用 `npm.cmd`，不要直接用 `npm`。

## 5. 当前未完成的功能

### 5.1 仍是占位页的后台模块

以下 4 个前端页面还没有做成真实 CRUD：

- 创作者管理：`ruoyi-ui/src/views/creative/creator/index.vue`
- 社区帖子：`ruoyi-ui/src/views/creative/post/index.vue`
- 评论管理：`ruoyi-ui/src/views/creative/comment/index.vue`
- 收藏管理：`ruoyi-ui/src/views/creative/favorite/index.vue`

建议下一步优先补：

1. 创作者管理
2. 社区帖子
3. 评论管理
4. 收藏管理

### 5.2 业务闭环未完成

当前只是后台基础 CRUD，还不是完整交易平台。未完成内容包括：

- 买家发布定制需求后的状态流转。
- 创作者对需求报价后的选中逻辑。
- 根据报价生成订单。
- 订单状态从已创建、制作中、已发货、已完成、已取消的流转限制。
- 商品上架、下架的业务校验。
- 评论与订单、商品、帖子之间的业务关联展示。
- 收藏功能的重复收藏校验。
- 创作者认证审核流程。
- 首页、商品详情、需求广场等前台页面。

### 5.3 数据展示仍较粗糙

当前页面为了快速形成基础框架，部分字段仍显示 ID：

- 商品页面显示 `creatorId`、`categoryId`
- 需求页面显示 `userId`、`categoryId`
- 报价页面显示 `demandId`、`creatorId`
- 订单页面显示 `buyerId`、`sellerId`

后续建议改成关联名称展示，例如：

- 分类名称
- 创作者昵称
- 买家昵称
- 需求标题
- 商品名称

### 5.4 权限和角色未细化

目前主要按若依后台菜单权限搭建，尚未区分业务角色：

- 平台管理员
- 普通买家
- 创作者/商家

后续建议：

- 管理员可管理全部数据。
- 买家只能管理自己的需求、订单、收藏。
- 创作者只能管理自己的商品、报价、订单。
- 敏感接口在后端加数据权限过滤，不只依赖前端隐藏按钮。

### 5.5 文件上传未接入业务

若依自带上传能力还没有和文创业务字段深度结合。未完成内容：

- 商品主图和轮播图。
- 创作者作品图。
- 定制需求参考图。
- 帖子图片。
- 评论图片。

### 5.6 前台用户端未开始

当前重点是后台管理端。前台用户端还未开发，包括：

- 首页
- 商品列表
- 商品详情
- 定制需求发布页
- 需求广场
- 创作者主页
- 订单中心
- 收藏中心
- 社区帖子列表和详情

## 6. 建议下次开发顺序

推荐按下面顺序继续，别东一榔头西一棒槌，最后把自己敲晕：

1. 完成剩余 4 个后台 CRUD 页面：创作者、帖子、评论、收藏。
2. 优化已完成 5 个页面的关联名称展示。
3. 补充业务状态流转接口：报价选中、生成订单、订单推进。
4. 接入图片上传字段。
5. 做前台首页和商品列表。
6. 做需求发布和报价流程。
7. 做订单中心和评价闭环。
8. 最后统一整理论文演示数据和答辩流程。

## 7. 常用检查命令

查看当前改动：

```powershell
git status --short
```

后端打包：

```powershell
cd D:\workspace\sihuo\RuoYi-Vue-master
mvn -pl ruoyi-admin -am -DskipTests package
```

前端构建：

```powershell
cd D:\workspace\sihuo\RuoYi-Vue-master\ruoyi-ui
npm.cmd run build:prod
```

检查端口：

```powershell
Get-NetTCPConnection -State Listen | Where-Object { $_.LocalPort -in 80,8080 }
```

## 8. 当前 Git 改动提醒

当前工作区已有修改，主要包括：

- 5 个文创业务 CRUD 前端页面。
- Redis 本地兜底缓存。
- 字典缓存兼容修复。
- 本进度交接文档。

提交前建议：

1. 再跑一次后端打包。
2. 再跑一次前端构建。
3. 手动登录后台，检查用户管理和文创菜单页面。
4. 确认 SQL 已导入且菜单权限可见。
5. 再执行 `git add`、`git commit`。

## 9. 重要约定

用户已说明：

- 不要自动启动前端或后端服务。
- 需要启动时，只给命令。
- 只有用户明确要求“帮我启动”，才实际启动服务。

