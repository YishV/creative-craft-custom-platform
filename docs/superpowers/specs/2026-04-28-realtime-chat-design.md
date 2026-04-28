# 实时在线沟通设计

## 背景

项目当前已经完成商品浏览、定制需求、报价、购物车、模拟支付、订单流转、社区和收藏关注等演示闭环。开题要求中的“在线沟通”仍主要由需求说明、报价说明和订单备注承载，缺少买家与创作者之间可实时交流、可留痕、可上传图片的沟通能力。

本设计补齐一个演示级实时聊天能力，效果接近电商客服沟通：买家和创作者可以围绕商品、需求或订单创建会话，发送文字和图片，双方在线时实时收到消息，离线或刷新后仍可查看历史记录。

## 目标

- 提供买家与创作者之间的一对一实时聊天。
- 支持文字消息和图片消息。
- 支持会话列表、历史消息、未读提示和标记已读。
- 聊天记录落库，便于答辩说明“交易沟通留痕、降低纠纷”。
- 复用现有通用上传接口 `/common/upload`，不新增文件存储链路。
- 优先服务毕设演示，不引入复杂客服分配、群聊、语音、撤回、已读回执详情等重功能。

## 非目标

- 不做真正淘宝级客服系统的排队、坐席、自动回复和工单。
- 不做 WebRTC、语音、视频、表情包市场。
- 不做管理员客服后台，后台仅通过数据库记录保留可扩展空间。
- 不做消息端到端加密。项目按毕设演示系统处理。

## 推荐方案

采用原生 WebSocket + REST 持久化：

- REST 接口负责会话创建、会话列表、历史消息、标记已读，以及 WebSocket 不可用时的兜底发送。
- WebSocket 地址为 `/ws/chat?token=xxx`，连接建立时解析 token，绑定当前登录用户。
- 发送消息时先校验用户是否属于会话，再写入数据库，最后推送给接收方和发送方。
- 图片消息先通过现有 `/common/upload` 上传，拿到 URL 后再作为图片消息发送。

该方案比轮询更符合“实时聊天”要求，比 STOMP/SockJS 更轻，适合当前 RuoYi-Vue 项目和毕设演示范围。

## 数据模型

### creative_chat_session

用于保存一对一会话。

核心字段：

- `session_id`：主键。
- `buyer_id`：买家用户 ID。
- `creator_id`：创作者档案 ID。
- `creator_user_id`：创作者对应系统用户 ID。
- `target_type`：关联对象类型，取值 `product`、`demand`、`order`。
- `target_id`：关联对象 ID。
- `target_name`：关联对象名称快照。
- `last_message`：最后一条消息摘要。
- `last_message_time`：最后消息时间。
- `buyer_unread`：买家未读数。
- `creator_unread`：创作者未读数。
- `status`：状态，默认 `0` 正常。
- `create_by`、`create_time`、`update_by`、`update_time`、`remark`：沿用 RuoYi 风格。

唯一性建议：`buyer_id + creator_user_id + target_type + target_id`，避免重复创建同一业务对象的会话。

### creative_chat_message

用于保存聊天消息。

核心字段：

- `message_id`：主键。
- `session_id`：会话 ID。
- `sender_id`：发送人用户 ID。
- `receiver_id`：接收人用户 ID。
- `message_type`：消息类型，取值 `text`、`image`。
- `content`：文字内容或图片 URL。
- `read_status`：读取状态，`0` 未读，`1` 已读。
- `create_by`、`create_time`：沿用 RuoYi 风格。

## 后端接口

新增 `CreativeChatController`，路径前缀 `/portal/chat`。

- `GET /portal/chat/session/list`
  返回当前用户参与的会话列表，按最后消息时间倒序。

- `POST /portal/chat/session`
  创建或打开会话。入参包含 `targetType`、`targetId`、可选 `creatorId`。商品会话可从商品查创作者；需求和订单会话按业务数据推导双方身份。

- `GET /portal/chat/message/list?sessionId=xxx`
  返回当前会话历史消息。仅会话参与者可查看。

- `POST /portal/chat/message`
  兜底发送消息。入参包含 `sessionId`、`messageType`、`content`。服务端完成权限校验、落库和推送。

- `POST /portal/chat/session/{sessionId}/read`
  当前用户进入会话后标记自己的未读消息为已读，并清零自己侧未读数。

## WebSocket

新增 WebSocket 地址 `/ws/chat`。

连接方式：

```text
ws://localhost:8080/ws/chat?token=<token>
```

消息发送格式：

```json
{
  "sessionId": 1,
  "messageType": "text",
  "content": "请问这个可以定制颜色吗？"
}
```

服务端推送格式：

```json
{
  "type": "message",
  "data": {
    "messageId": 1001,
    "sessionId": 1,
    "senderId": 2,
    "receiverId": 5,
    "messageType": "text",
    "content": "可以定制。",
    "createTime": "2026-04-28 14:30:00"
  }
}
```

异常消息格式：

```json
{
  "type": "error",
  "message": "无权访问该会话"
}
```

## 权限规则

- 买家只能访问自己作为 `buyer_id` 的会话。
- 创作者只能访问 `creator_user_id` 等于自己的会话。
- 创建商品会话时，商品必须存在且上架，创作者必须有效。
- 创建需求会话时，买家为需求发布人，创作者为主动联系或报价方。
- 创建订单会话时，买家和创作者来自订单双方。
- 发送消息时服务端重新校验会话参与关系，不能只信任前端传参。

## 前端页面

新增 `/portal/chat` 页面。

布局：

- 左侧为会话列表：展示关联对象名称、最后消息、时间、未读数。
- 右侧为聊天窗口：展示消息气泡，自己在右侧，对方在左侧。
- 底部输入区：文本框、图片上传按钮、发送按钮。

入口：

- 商品详情页增加“联系创作者”。
- 需求详情或需求广场操作区增加“在线沟通”。
- 订单中心增加“联系对方”。
- 前台首页或个人入口可增加“消息”快捷入口。

图片：

- 使用 `el-upload` 调用 `/common/upload`。
- 上传成功后发送 `messageType=image`，`content` 为返回 URL。
- 图片气泡限制显示尺寸，点击可预览。

连接策略：

- 页面进入后建立 WebSocket。
- WebSocket 断开时最多自动重连 3 次。
- 发送失败时提示用户，并可调用 REST 兜底接口发送。

## 测试与验证

后端验证：

- 会话创建：商品、订单场景至少各一条。
- 权限校验：非参与者不能查看或发送消息。
- 消息落库：文字和图片消息都能写入。
- WebSocket 推送：双方在线时接收方实时收到消息。

前端验证：

- `npm.cmd run build:prod` 通过。
- 两个浏览器或一个普通窗口加一个无痕窗口登录不同账号，能实时互发文字。
- 上传图片后对方能实时看到图片气泡。
- 刷新页面后历史消息仍存在。

后端打包：

```powershell
mvn -pl ruoyi-admin -am -DskipTests package
```

## 文档更新

实现完成后同步更新：

- `docs/collaboration.md`：推荐算法从优先项移除，阶段 5 调整为在线沟通。
- `README.md`：功能范围和开题需求对照改为“在线沟通已完成，推荐算法不作为当前范围”。
- `sql/README.md`：增加聊天 SQL 脚本执行说明。

