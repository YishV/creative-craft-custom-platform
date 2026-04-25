# 后端数据权限设计

日期：`2026-04-25`

## 目标

完成阶段 3 的“后端数据权限”子任务，范围限定为：
- 查询过滤：买家只能看到自己的需求、收藏、买家订单；创作者只能看到自己的商品、报价、卖家订单
- 写操作校验：修改、删除、状态推进、选中报价、上下架等动作只能操作本人数据
- 管理员保持现有全量访问能力

本轮不包含：
- 前端菜单隐藏、按钮显隐
- `/portal` 用户端页面
- 若依 `@DataScope` 扩展改造

## 当前现状

- 文创模块当前主要依赖菜单权限 `creative:*:*` 控制是否可访问接口
- 各业务 Service 基本都是直接 CRUD，没有结合当前登录用户做业务归属限制
- 列表查询虽然已有归属字段，但控制器不会主动按当前用户收敛条件
- 详情、修改、删除、状态流转、选中报价等按 ID 的接口也没有所有权校验

现有业务归属字段如下：
- `creative_demand.user_id`：需求归属买家
- `creative_favorite.user_id`：收藏归属用户
- `creative_order.buyer_id` / `seller_id`：订单买卖双方
- `creative_product.creator_id`：商品归属创作者
- `creative_quote.creator_id`：报价归属创作者
- `creative_creator.user_id`：系统用户与创作者档案的映射

## 方案比较

### 方案 A：只在 Controller 填查询条件

- 优点：改动少，容易快速生效到列表接口
- 缺点：容易漏掉详情、删除、状态流转、选中报价等非列表入口
- 缺点：规则分散在多个 Controller，后续维护成本高

### 方案 B：Service 层统一做归属校验

- 优点：查询、详情、修改、删除、状态流转都能复用同一套规则
- 优点：可以覆盖“列表看不到，但直接调详情接口仍能访问”的漏洞
- 优点：边界清晰，后续前端隐藏只是体验增强，不承担安全职责
- 缺点：需要引入一个轻量的业务权限辅助组件

### 方案 C：接入 MyBatis / `@DataScope` 数据域机制

- 优点：从框架层做统一数据权限，形式更标准
- 缺点：当前文创模块不是按部门/角色数据域建模，buyer/creator 双视角订单也不适合硬套现有部门过滤
- 缺点：实现复杂度明显高于这轮阶段目标

**结论：采用方案 B。**

## 设计

### 统一权限边界

- `admin` 视为平台管理员，所有文创数据不过滤
- `buyer` 按当前登录 `userId` 过滤自身业务数据
- `creator` 先通过 `creative_creator.user_id -> creator_id` 找到本人创作者档案，再按 `creatorId` 过滤
- 同一用户若同时拥有 `buyer + creator`，订单详情和列表按“买家或卖家任一命中即可访问”

### 权限辅助组件

新增一个轻量业务权限辅助组件，职责限定为：
- 获取当前登录 `userId`
- 判断当前用户是否管理员
- 查询当前用户绑定的有效 `creatorId`
- 提供统一断言方法，用于校验某条记录是否属于当前用户

不把真实业务规则塞进 Controller，也不引入重量级切面；组件只负责“当前用户是谁，以及他可以碰哪些数据”。

### Controller 责任

Controller 只负责把新增场景的归属字段补齐：
- 新增需求、收藏时，强制写入当前 `userId`
- 新增商品、报价时，强制写入当前 `creatorId`
- 不信任前端上传的 `userId`、`creatorId`

查询、详情、修改、删除、状态流转的真正权限判断全部下沉到 Service。

### 买家规则

- `CreativeDemandService`
  - `list` 自动收敛为当前用户自己的需求
  - `get/update/delete/transitDemandStatus` 仅允许操作本人需求
- `CreativeFavoriteService`
  - `list` 自动收敛为当前用户自己的收藏
  - `get/update/delete` 仅允许操作本人收藏
- `CreativeOrderService`
  - `list` 对买家视角只返回 `buyerId = currentUserId`
  - `get/transitOrderStatus` 仅允许访问或推进本人买家订单，管理员例外
- `CreativeQuoteService.selectQuoteAndGenerateOrder`
  - 当前用户必须是报价对应需求的发布者，才能选中报价并生成订单

### 创作者规则

- `CreativeProductService`
  - `list` 自动收敛为当前创作者自己的商品
  - `get/update/delete/putOnShelf/takeOffShelf` 仅允许操作本人商品
- `CreativeQuoteService`
  - `list` 自动收敛为当前创作者自己的报价
  - `insert` 强制写入当前 `creatorId`
  - `get/update/delete` 仅允许操作本人报价
- `CreativeOrderService`
  - `list` 对创作者视角只返回 `sellerId = currentCreatorId`
  - `get/transitOrderStatus` 仅允许访问或推进本人卖家订单，管理员例外

### 创作者身份解析

商品、报价、卖家订单都依赖 `creatorId`，但登录态里只有 `userId`。因此需要统一走：

`current userId -> creative_creator.user_id -> creator_id`

解析规则：
- 只有审核通过且状态正常的创作者档案，才视为有效创作者身份
- 没有有效创作者档案时：
  - 查询商品/报价/卖家订单列表返回空
  - 新增商品/报价、修改本人创作者数据、上下架等创作者专属动作直接报错

## 错误处理

- 非本人数据访问统一抛出 `ServiceException("无权操作该数据")`
- 当前用户未绑定有效创作者身份时，抛出 `ServiceException("当前用户未绑定有效创作者身份")`
- 批量删除逐条校验，只要其中一条不属于当前用户，就整体拒绝，避免部分成功造成歧义

## 风险与取舍

- 订单当前同时存在买家和卖家两个业务主体，本轮不拆“买家可做哪些流转、卖家可做哪些流转”的更细粒度动作权限，只先保证“只能操作与自己有关的订单”
- 创作者列表在没有有效创作者档案时直接返回空，而不是报错，便于后台页平稳加载
- 这套后端权限不会自动影响前端显示；前端按钮隐藏仍可作为后续体验优化单独处理

## 测试策略

在 `ruoyi-system` 补 service 层单元测试，至少覆盖：
- 买家列表只能看到自己的需求、收藏、订单
- 创作者列表只能看到自己的商品、报价、订单
- 非本人修改、删除、状态推进失败
- 买家不能选中他人需求的报价
- 创作者不能上下架他人商品
- 无有效创作者身份时，创作者新增或修改动作失败
- 管理员查询和操作不受上述限制
