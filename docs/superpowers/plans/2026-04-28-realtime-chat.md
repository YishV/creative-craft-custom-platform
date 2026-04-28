# Realtime Chat Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a simple real-time buyer-to-creator chat feature with text messages, image messages, persisted history, unread counts, and portal entry points.

**Architecture:** Add two chat tables in `ruoyi-system`, expose REST endpoints under `/portal/chat`, and add a native Spring WebSocket endpoint at `/ws/chat` for real-time message delivery. The Vue portal adds a two-pane chat page and opens or reuses sessions from product, demand, and order flows.

**Tech Stack:** Spring Boot 3, Spring WebSocket, MyBatis XML mappers, RuoYi token/Redis auth, Vue 2, Element UI, existing `/common/upload`.

---

## File Structure

- Create `sql/chat_upgrade_20260428.sql`
  Creates `creative_chat_session` and `creative_chat_message`.
- Modify `sql/README.md`
  Adds chat SQL execution order.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSession.java`
  Domain object for conversation rows and display fields.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessage.java`
  Domain object for message rows.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSessionRequest.java`
  Request body for opening a chat session.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessageRequest.java`
  Request body for sending a message.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatSessionMapper.java`
  MyBatis mapper for chat sessions.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatMessageMapper.java`
  MyBatis mapper for chat messages.
- Create `ruoyi-system/src/main/resources/mapper/creative/CreativeChatSessionMapper.xml`
  Session SQL queries and updates.
- Create `ruoyi-system/src/main/resources/mapper/creative/CreativeChatMessageMapper.xml`
  Message SQL queries and updates.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/ICreativeChatService.java`
  Chat service interface used by REST and WebSocket.
- Create `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImpl.java`
  Session opening, permission checks, message persistence, unread updates.
- Create `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImplTest.java`
  Unit tests for session creation, permissions, and message persistence.
- Modify `ruoyi-admin/pom.xml`
  Adds `spring-boot-starter-websocket`.
- Create `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeChatController.java`
  Portal chat REST endpoints.
- Create `ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketConfig.java`
  Registers `/ws/chat`.
- Create `ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketHandler.java`
  Handles connect, receive, send, disconnect.
- Modify `ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java`
  Adds `getLoginUser(String token)` helper for WebSocket query-token auth.
- Modify `ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java`
  Permits `/ws/chat/**` handshake, while handler performs token validation.
- Create `ruoyi-ui/src/api/creative/chat.js`
  REST API wrappers.
- Create `ruoyi-ui/src/utils/chatSocket.js`
  WebSocket client wrapper with token and reconnect.
- Create `ruoyi-ui/src/views/portal/chat.vue`
  Chat page.
- Modify `ruoyi-ui/src/router/index.js`
  Adds `/portal/chat`.
- Modify `ruoyi-ui/src/views/portal/products.vue`
  Adds “联系创作者” entry.
- Modify `ruoyi-ui/src/views/portal/demands.vue`
  Adds “在线沟通” entry.
- Modify `ruoyi-ui/src/views/portal/orders.vue`
  Adds “联系对方” entry.
- Modify `ruoyi-ui/src/views/portal/index.vue`
  Adds message center quick entry.
- Modify `ruoyi-ui/vue.config.js`
  Adds WebSocket proxy support for `/ws`.
- Modify `README.md` and `docs/collaboration.md`
  Updates project scope from recommendation algorithm to online communication.

---

### Task 1: Database Schema

**Files:**
- Create: `sql/chat_upgrade_20260428.sql`
- Modify: `sql/README.md`

- [ ] **Step 1: Create the chat SQL script**

Add this exact file:

```sql
-- 实时在线沟通表结构
-- 执行日期：2026-04-28

create table if not exists creative_chat_session (
  session_id bigint(20) not null auto_increment comment '会话ID',
  buyer_id bigint(20) not null comment '买家用户ID',
  creator_id bigint(20) default null comment '创作者档案ID',
  creator_user_id bigint(20) not null comment '创作者用户ID',
  target_type varchar(20) not null comment '关联类型：product/demand/order',
  target_id bigint(20) not null comment '关联对象ID',
  target_name varchar(120) default '' comment '关联对象名称快照',
  last_message varchar(500) default '' comment '最后消息摘要',
  last_message_time datetime default null comment '最后消息时间',
  buyer_unread int(11) default 0 comment '买家未读数',
  creator_unread int(11) default 0 comment '创作者未读数',
  status char(1) default '0' comment '状态（0正常 1停用）',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  remark varchar(500) default null comment '备注',
  primary key (session_id),
  unique key uk_chat_session_target (buyer_id, creator_user_id, target_type, target_id),
  key idx_chat_session_buyer (buyer_id),
  key idx_chat_session_creator_user (creator_user_id),
  key idx_chat_session_last_time (last_message_time)
) engine=innodb auto_increment=1 comment='文创聊天会话表';

create table if not exists creative_chat_message (
  message_id bigint(20) not null auto_increment comment '消息ID',
  session_id bigint(20) not null comment '会话ID',
  sender_id bigint(20) not null comment '发送人用户ID',
  receiver_id bigint(20) not null comment '接收人用户ID',
  message_type varchar(20) not null comment '消息类型：text/image',
  content varchar(1000) not null comment '消息内容或图片URL',
  read_status char(1) default '0' comment '读取状态（0未读 1已读）',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default null comment '创建时间',
  primary key (message_id),
  key idx_chat_message_session (session_id, message_id),
  key idx_chat_message_receiver_read (receiver_id, read_status)
) engine=innodb auto_increment=1 comment='文创聊天消息表';
```

- [ ] **Step 2: Update SQL documentation**

In `sql/README.md`, add `chat_upgrade_20260428.sql` after `order_payment_upgrade_20260428.sql` in both new-database and old-database execution sections:

```sql
source sql/chat_upgrade_20260428.sql;
```

Add a short description:

```markdown
| `chat_upgrade_20260428.sql` | 新增实时在线沟通会话表和消息表，支持文字、图片、历史记录和未读数。 |
```

- [ ] **Step 3: Verify SQL script is present**

Run:

```powershell
Get-Content sql\chat_upgrade_20260428.sql -TotalCount 80
```

Expected: output includes `creative_chat_session` and `creative_chat_message`.

- [ ] **Step 4: Commit database task**

```powershell
git add sql/chat_upgrade_20260428.sql sql/README.md
git commit -m "feat(creative): 新增聊天表结构 [Codex]"
```

---

### Task 2: Domain Objects And MyBatis Mappers

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSession.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessage.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSessionRequest.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessageRequest.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatSessionMapper.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatMessageMapper.java`
- Create: `ruoyi-system/src/main/resources/mapper/creative/CreativeChatSessionMapper.xml`
- Create: `ruoyi-system/src/main/resources/mapper/creative/CreativeChatMessageMapper.xml`

- [ ] **Step 1: Create `CreativeChatSession.java`**

```java
package com.ruoyi.system.domain.creative;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class CreativeChatSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Long buyerId;
    private Long creatorId;
    private Long creatorUserId;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    private Integer buyerUnread;
    private Integer creatorUnread;
    private String status;
    private String peerName;
    private Long peerUserId;
    private Integer unreadCount;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Long creatorUserId) { this.creatorUserId = creatorUserId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public Date getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Date lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public Integer getBuyerUnread() { return buyerUnread; }
    public void setBuyerUnread(Integer buyerUnread) { this.buyerUnread = buyerUnread; }
    public Integer getCreatorUnread() { return creatorUnread; }
    public void setCreatorUnread(Integer creatorUnread) { this.creatorUnread = creatorUnread; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPeerName() { return peerName; }
    public void setPeerName(String peerName) { this.peerName = peerName; }
    public Long getPeerUserId() { return peerUserId; }
    public void setPeerUserId(Long peerUserId) { this.peerUserId = peerUserId; }
    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
}
```

- [ ] **Step 2: Create `CreativeChatMessage.java`**

```java
package com.ruoyi.system.domain.creative;

import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long sessionId;
    private Long senderId;
    private Long receiverId;
    private String messageType;
    private String content;
    private String readStatus;

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReadStatus() { return readStatus; }
    public void setReadStatus(String readStatus) { this.readStatus = readStatus; }
}
```

- [ ] **Step 3: Create request classes**

`CreativeChatSessionRequest.java`:

```java
package com.ruoyi.system.domain.creative;

import java.io.Serializable;

public class CreativeChatSessionRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String targetType;
    private Long targetId;
    private Long creatorId;

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
}
```

`CreativeChatMessageRequest.java`:

```java
package com.ruoyi.system.domain.creative;

import java.io.Serializable;

public class CreativeChatMessageRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private String messageType;
    private String content;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
```

- [ ] **Step 4: Create mapper interfaces**

`CreativeChatSessionMapper.java`:

```java
package com.ruoyi.system.mapper.creative;

import com.ruoyi.system.domain.creative.CreativeChatSession;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CreativeChatSessionMapper
{
    CreativeChatSession selectCreativeChatSessionBySessionId(Long sessionId);

    CreativeChatSession selectExistingSession(@Param("buyerId") Long buyerId,
            @Param("creatorUserId") Long creatorUserId,
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId);

    List<CreativeChatSession> selectSessionListByUserId(Long userId);

    int insertCreativeChatSession(CreativeChatSession session);

    int updateCreativeChatSession(CreativeChatSession session);

    int incrementUnread(@Param("sessionId") Long sessionId, @Param("fieldName") String fieldName);

    int clearUnread(@Param("sessionId") Long sessionId, @Param("fieldName") String fieldName);
}
```

`CreativeChatMessageMapper.java`:

```java
package com.ruoyi.system.mapper.creative;

import com.ruoyi.system.domain.creative.CreativeChatMessage;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CreativeChatMessageMapper
{
    CreativeChatMessage selectCreativeChatMessageByMessageId(Long messageId);

    List<CreativeChatMessage> selectMessagesBySessionId(Long sessionId);

    int insertCreativeChatMessage(CreativeChatMessage message);

    int markSessionMessagesRead(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
}
```

- [ ] **Step 5: Create mapper XML files**

Create `CreativeChatSessionMapper.xml` with explicit field mapping and field-name guarded unread updates:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.creative.CreativeChatSessionMapper">
    <resultMap id="CreativeChatSessionResult" type="com.ruoyi.system.domain.creative.CreativeChatSession">
        <result property="sessionId" column="session_id"/>
        <result property="buyerId" column="buyer_id"/>
        <result property="creatorId" column="creator_id"/>
        <result property="creatorUserId" column="creator_user_id"/>
        <result property="targetType" column="target_type"/>
        <result property="targetId" column="target_id"/>
        <result property="targetName" column="target_name"/>
        <result property="lastMessage" column="last_message"/>
        <result property="lastMessageTime" column="last_message_time"/>
        <result property="buyerUnread" column="buyer_unread"/>
        <result property="creatorUnread" column="creator_unread"/>
        <result property="status" column="status"/>
        <result property="createBy" column="create_by"/>
        <result property="createTime" column="create_time"/>
        <result property="updateBy" column="update_by"/>
        <result property="updateTime" column="update_time"/>
        <result property="remark" column="remark"/>
    </resultMap>

    <sql id="selectCreativeChatSessionVo">
        select session_id, buyer_id, creator_id, creator_user_id, target_type, target_id,
               target_name, last_message, last_message_time, buyer_unread, creator_unread,
               status, create_by, create_time, update_by, update_time, remark
        from creative_chat_session
    </sql>

    <select id="selectCreativeChatSessionBySessionId" parameterType="Long" resultMap="CreativeChatSessionResult">
        <include refid="selectCreativeChatSessionVo"/>
        where session_id = #{sessionId}
    </select>

    <select id="selectExistingSession" resultMap="CreativeChatSessionResult">
        <include refid="selectCreativeChatSessionVo"/>
        where buyer_id = #{buyerId}
          and creator_user_id = #{creatorUserId}
          and target_type = #{targetType}
          and target_id = #{targetId}
        limit 1
    </select>

    <select id="selectSessionListByUserId" parameterType="Long" resultMap="CreativeChatSessionResult">
        <include refid="selectCreativeChatSessionVo"/>
        where status = '0'
          and (buyer_id = #{userId} or creator_user_id = #{userId})
        order by ifnull(last_message_time, create_time) desc, session_id desc
    </select>

    <insert id="insertCreativeChatSession" parameterType="com.ruoyi.system.domain.creative.CreativeChatSession" useGeneratedKeys="true" keyProperty="sessionId">
        insert into creative_chat_session
        (buyer_id, creator_id, creator_user_id, target_type, target_id, target_name, last_message,
         last_message_time, buyer_unread, creator_unread, status, create_by, create_time, remark)
        values
        (#{buyerId}, #{creatorId}, #{creatorUserId}, #{targetType}, #{targetId}, #{targetName}, #{lastMessage},
         #{lastMessageTime}, #{buyerUnread}, #{creatorUnread}, #{status}, #{createBy}, sysdate(), #{remark})
    </insert>

    <update id="updateCreativeChatSession" parameterType="com.ruoyi.system.domain.creative.CreativeChatSession">
        update creative_chat_session
        <set>
            <if test="lastMessage != null">last_message = #{lastMessage},</if>
            <if test="lastMessageTime != null">last_message_time = #{lastMessageTime},</if>
            <if test="buyerUnread != null">buyer_unread = #{buyerUnread},</if>
            <if test="creatorUnread != null">creator_unread = #{creatorUnread},</if>
            <if test="status != null">status = #{status},</if>
            <if test="remark != null">remark = #{remark},</if>
            update_by = #{updateBy},
            update_time = sysdate()
        </set>
        where session_id = #{sessionId}
    </update>

    <update id="incrementUnread">
        update creative_chat_session
        <set>
            <choose>
                <when test="fieldName == 'buyer_unread'">buyer_unread = buyer_unread + 1,</when>
                <otherwise>creator_unread = creator_unread + 1,</otherwise>
            </choose>
            update_time = sysdate()
        </set>
        where session_id = #{sessionId}
    </update>

    <update id="clearUnread">
        update creative_chat_session
        <set>
            <choose>
                <when test="fieldName == 'buyer_unread'">buyer_unread = 0,</when>
                <otherwise>creator_unread = 0,</otherwise>
            </choose>
            update_time = sysdate()
        </set>
        where session_id = #{sessionId}
    </update>
</mapper>
```

Create `CreativeChatMessageMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.creative.CreativeChatMessageMapper">
    <resultMap id="CreativeChatMessageResult" type="com.ruoyi.system.domain.creative.CreativeChatMessage">
        <result property="messageId" column="message_id"/>
        <result property="sessionId" column="session_id"/>
        <result property="senderId" column="sender_id"/>
        <result property="receiverId" column="receiver_id"/>
        <result property="messageType" column="message_type"/>
        <result property="content" column="content"/>
        <result property="readStatus" column="read_status"/>
        <result property="createBy" column="create_by"/>
        <result property="createTime" column="create_time"/>
    </resultMap>

    <sql id="selectCreativeChatMessageVo">
        select message_id, session_id, sender_id, receiver_id, message_type, content,
               read_status, create_by, create_time
        from creative_chat_message
    </sql>

    <select id="selectCreativeChatMessageByMessageId" parameterType="Long" resultMap="CreativeChatMessageResult">
        <include refid="selectCreativeChatMessageVo"/>
        where message_id = #{messageId}
    </select>

    <select id="selectMessagesBySessionId" parameterType="Long" resultMap="CreativeChatMessageResult">
        <include refid="selectCreativeChatMessageVo"/>
        where session_id = #{sessionId}
        order by message_id asc
    </select>

    <insert id="insertCreativeChatMessage" parameterType="com.ruoyi.system.domain.creative.CreativeChatMessage" useGeneratedKeys="true" keyProperty="messageId">
        insert into creative_chat_message
        (session_id, sender_id, receiver_id, message_type, content, read_status, create_by, create_time)
        values
        (#{sessionId}, #{senderId}, #{receiverId}, #{messageType}, #{content}, #{readStatus}, #{createBy}, sysdate())
    </insert>

    <update id="markSessionMessagesRead">
        update creative_chat_message
        set read_status = '1'
        where session_id = #{sessionId}
          and receiver_id = #{receiverId}
          and read_status = '0'
    </update>
</mapper>
```

- [ ] **Step 6: Compile mapper/domain changes**

Run:

```powershell
mvn -pl ruoyi-system -DskipTests compile
```

Expected: build success.

- [ ] **Step 7: Commit mapper task**

```powershell
git add ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSession.java ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessage.java ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatSessionRequest.java ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/CreativeChatMessageRequest.java ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatSessionMapper.java ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/CreativeChatMessageMapper.java ruoyi-system/src/main/resources/mapper/creative/CreativeChatSessionMapper.xml ruoyi-system/src/main/resources/mapper/creative/CreativeChatMessageMapper.xml
git commit -m "feat(creative): 新增聊天领域模型和Mapper [Codex]"
```

---

### Task 3: Chat Service And Unit Tests

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/ICreativeChatService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImpl.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImplTest.java`

- [ ] **Step 1: Write failing unit tests**

Create `CreativeChatServiceImplTest.java` with these tests:

```java
package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeChatMessageMapper;
import com.ruoyi.system.mapper.creative.CreativeChatSessionMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeChatServiceImplTest
{
    @Mock private CreativeChatSessionMapper sessionMapper;
    @Mock private CreativeChatMessageMapper messageMapper;
    @Mock private CreativeProductMapper productMapper;
    @Mock private CreativeCreatorMapper creatorMapper;
    @Mock private CreativeDemandMapper demandMapper;
    @Mock private CreativeOrderMapper orderMapper;

    @InjectMocks
    private CreativeChatServiceImpl chatService;

    @Test
    void openSessionShouldCreateProductSession()
    {
        CreativeProduct product = new CreativeProduct();
        product.setProductId(10L);
        product.setProductName("手作杯垫");
        product.setCreatorId(3L);
        product.setPrice(BigDecimal.TEN);
        product.setStatus("0");
        CreativeCreator creator = new CreativeCreator();
        creator.setCreatorId(3L);
        creator.setUserId(8L);
        creator.setCreatorName("木作匠人");
        creator.setStatus("0");
        creator.setAuditStatus("approved");
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(product);
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(creator);

        CreativeChatSessionRequest request = new CreativeChatSessionRequest();
        request.setTargetType("product");
        request.setTargetId(10L);

        CreativeChatSession result = chatService.openSession(request, 5L, "buyer");

        ArgumentCaptor<CreativeChatSession> captor = ArgumentCaptor.forClass(CreativeChatSession.class);
        verify(sessionMapper).insertCreativeChatSession(captor.capture());
        assertEquals(5L, captor.getValue().getBuyerId());
        assertEquals(3L, captor.getValue().getCreatorId());
        assertEquals(8L, captor.getValue().getCreatorUserId());
        assertEquals("手作杯垫", captor.getValue().getTargetName());
        assertEquals(captor.getValue(), result);
    }

    @Test
    void sendMessageShouldRejectNonParticipant()
    {
        CreativeChatSession session = new CreativeChatSession();
        session.setSessionId(1L);
        session.setBuyerId(5L);
        session.setCreatorUserId(8L);
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(session);

        CreativeChatMessageRequest request = new CreativeChatMessageRequest();
        request.setSessionId(1L);
        request.setMessageType("text");
        request.setContent("你好");

        assertThrows(ServiceException.class, () -> chatService.sendMessage(request, 99L, "stranger"));
        verify(messageMapper, never()).insertCreativeChatMessage(any(CreativeChatMessage.class));
    }

    @Test
    void sendMessageShouldPersistAndIncrementReceiverUnread()
    {
        CreativeChatSession session = new CreativeChatSession();
        session.setSessionId(1L);
        session.setBuyerId(5L);
        session.setCreatorUserId(8L);
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(session);

        CreativeChatMessageRequest request = new CreativeChatMessageRequest();
        request.setSessionId(1L);
        request.setMessageType("text");
        request.setContent("可以定制颜色吗");

        CreativeChatMessage message = chatService.sendMessage(request, 5L, "buyer");

        ArgumentCaptor<CreativeChatMessage> captor = ArgumentCaptor.forClass(CreativeChatMessage.class);
        verify(messageMapper).insertCreativeChatMessage(captor.capture());
        verify(sessionMapper).incrementUnread(1L, "creator_unread");
        assertEquals(8L, captor.getValue().getReceiverId());
        assertEquals("可以定制颜色吗", message.getContent());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
mvn -pl ruoyi-system -Dtest=CreativeChatServiceImplTest test
```

Expected: fail because `CreativeChatServiceImpl` and `ICreativeChatService` do not exist.

- [ ] **Step 3: Create service interface**

```java
package com.ruoyi.system.service.creative;

import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import java.util.List;

public interface ICreativeChatService
{
    List<CreativeChatSession> listMySessions(Long userId);

    CreativeChatSession openSession(CreativeChatSessionRequest request, Long currentUserId, String username);

    List<CreativeChatMessage> listMessages(Long sessionId, Long currentUserId);

    CreativeChatMessage sendMessage(CreativeChatMessageRequest request, Long currentUserId, String username);

    int markRead(Long sessionId, Long currentUserId);
}
```

- [ ] **Step 4: Create service implementation**

Implement `CreativeChatServiceImpl.java` with constants, strict permission checks, message validation, and product session support first. Demand and order support must be implemented in Task 6 entry integration if the existing fields require local adjustment.

```java
package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeChatMessageMapper;
import com.ruoyi.system.mapper.creative.CreativeChatSessionMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeChatService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeChatServiceImpl implements ICreativeChatService
{
    private static final String STATUS_NORMAL = "0";
    private static final String AUDIT_APPROVED = "approved";
    private static final String TYPE_PRODUCT = "product";
    private static final String MESSAGE_TEXT = "text";
    private static final String MESSAGE_IMAGE = "image";
    private static final String READ_NO = "0";

    @Autowired private CreativeChatSessionMapper sessionMapper;
    @Autowired private CreativeChatMessageMapper messageMapper;
    @Autowired private CreativeProductMapper productMapper;
    @Autowired private CreativeCreatorMapper creatorMapper;
    @Autowired private CreativeDemandMapper demandMapper;
    @Autowired private CreativeOrderMapper orderMapper;

    @Override
    public List<CreativeChatSession> listMySessions(Long userId)
    {
        List<CreativeChatSession> sessions = sessionMapper.selectSessionListByUserId(userId);
        for (CreativeChatSession session : sessions)
        {
            fillSessionView(session, userId);
        }
        return sessions;
    }

    @Override
    public CreativeChatSession openSession(CreativeChatSessionRequest request, Long currentUserId, String username)
    {
        validateSessionRequest(request);
        if (TYPE_PRODUCT.equals(request.getTargetType()))
        {
            return openProductSession(request.getTargetId(), currentUserId, username);
        }
        throw new ServiceException("暂不支持该聊天入口");
    }

    @Override
    public List<CreativeChatMessage> listMessages(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = requireParticipant(sessionId, currentUserId);
        markRead(session.getSessionId(), currentUserId);
        return messageMapper.selectMessagesBySessionId(sessionId);
    }

    @Override
    public CreativeChatMessage sendMessage(CreativeChatMessageRequest request, Long currentUserId, String username)
    {
        validateMessageRequest(request);
        CreativeChatSession session = requireParticipant(request.getSessionId(), currentUserId);
        Long receiverId = session.getBuyerId().equals(currentUserId) ? session.getCreatorUserId() : session.getBuyerId();

        CreativeChatMessage message = new CreativeChatMessage();
        message.setSessionId(session.getSessionId());
        message.setSenderId(currentUserId);
        message.setReceiverId(receiverId);
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent().trim());
        message.setReadStatus(READ_NO);
        message.setCreateBy(username);
        messageMapper.insertCreativeChatMessage(message);

        CreativeChatSession update = new CreativeChatSession();
        update.setSessionId(session.getSessionId());
        update.setLastMessage(MESSAGE_IMAGE.equals(request.getMessageType()) ? "[图片]" : abbreviate(request.getContent().trim()));
        update.setLastMessageTime(new Date());
        update.setUpdateBy(username);
        sessionMapper.updateCreativeChatSession(update);
        sessionMapper.incrementUnread(session.getSessionId(), session.getBuyerId().equals(receiverId) ? "buyer_unread" : "creator_unread");
        return message;
    }

    @Override
    public int markRead(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = requireParticipant(sessionId, currentUserId);
        String unreadField = session.getBuyerId().equals(currentUserId) ? "buyer_unread" : "creator_unread";
        messageMapper.markSessionMessagesRead(sessionId, currentUserId);
        return sessionMapper.clearUnread(sessionId, unreadField);
    }

    private CreativeChatSession openProductSession(Long productId, Long currentUserId, String username)
    {
        CreativeProduct product = productMapper.selectCreativeProductByProductId(productId);
        if (product == null || !STATUS_NORMAL.equals(product.getStatus()))
        {
            throw new ServiceException("商品不存在或已下架");
        }
        CreativeCreator creator = creatorMapper.selectCreativeCreatorByCreatorId(product.getCreatorId());
        if (creator == null || creator.getUserId() == null || !STATUS_NORMAL.equals(creator.getStatus())
                || !AUDIT_APPROVED.equals(creator.getAuditStatus()))
        {
            throw new ServiceException("创作者不存在或未通过审核");
        }
        if (creator.getUserId().equals(currentUserId))
        {
            throw new ServiceException("不能和自己发起聊天");
        }
        CreativeChatSession existing = sessionMapper.selectExistingSession(currentUserId, creator.getUserId(), TYPE_PRODUCT, productId);
        if (existing != null)
        {
            fillSessionView(existing, currentUserId);
            return existing;
        }
        CreativeChatSession session = new CreativeChatSession();
        session.setBuyerId(currentUserId);
        session.setCreatorId(creator.getCreatorId());
        session.setCreatorUserId(creator.getUserId());
        session.setTargetType(TYPE_PRODUCT);
        session.setTargetId(productId);
        session.setTargetName(product.getProductName());
        session.setBuyerUnread(0);
        session.setCreatorUnread(0);
        session.setStatus(STATUS_NORMAL);
        session.setCreateBy(username);
        sessionMapper.insertCreativeChatSession(session);
        fillSessionView(session, currentUserId);
        return session;
    }

    private CreativeChatSession requireParticipant(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = sessionMapper.selectCreativeChatSessionBySessionId(sessionId);
        if (session == null || (!currentUserId.equals(session.getBuyerId()) && !currentUserId.equals(session.getCreatorUserId())))
        {
            throw new ServiceException("无权访问该会话");
        }
        return session;
    }

    private void validateSessionRequest(CreativeChatSessionRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getTargetType()) || request.getTargetId() == null)
        {
            throw new ServiceException("聊天入口参数不完整");
        }
    }

    private void validateMessageRequest(CreativeChatMessageRequest request)
    {
        if (request == null || request.getSessionId() == null || StringUtils.isEmpty(request.getMessageType())
                || StringUtils.isEmpty(request.getContent()))
        {
            throw new ServiceException("消息内容不能为空");
        }
        if (!MESSAGE_TEXT.equals(request.getMessageType()) && !MESSAGE_IMAGE.equals(request.getMessageType()))
        {
            throw new ServiceException("不支持的消息类型");
        }
        if (request.getContent().trim().length() > 1000)
        {
            throw new ServiceException("消息内容不能超过1000字");
        }
    }

    private String abbreviate(String text)
    {
        return text.length() > 80 ? text.substring(0, 80) : text;
    }

    private void fillSessionView(CreativeChatSession session, Long currentUserId)
    {
        if (session.getBuyerId() != null && session.getBuyerId().equals(currentUserId))
        {
            session.setPeerUserId(session.getCreatorUserId());
            session.setPeerName("创作者 #" + session.getCreatorId());
            session.setUnreadCount(session.getBuyerUnread());
        }
        else
        {
            session.setPeerUserId(session.getBuyerId());
            session.setPeerName("买家 #" + session.getBuyerId());
            session.setUnreadCount(session.getCreatorUnread());
        }
    }
}
```

- [ ] **Step 5: Run tests**

```powershell
mvn -pl ruoyi-system -Dtest=CreativeChatServiceImplTest test
```

Expected: tests pass.

- [ ] **Step 6: Commit service task**

```powershell
git add ruoyi-system/src/main/java/com/ruoyi/system/service/creative/ICreativeChatService.java ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImpl.java ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeChatServiceImplTest.java
git commit -m "feat(creative): 实现聊天服务核心逻辑 [Codex]"
```

---

### Task 4: REST Controller And WebSocket Backend

**Files:**
- Modify: `ruoyi-admin/pom.xml`
- Modify: `ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java`
- Modify: `ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeChatController.java`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketConfig.java`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketHandler.java`

- [ ] **Step 1: Add WebSocket dependency**

In `ruoyi-admin/pom.xml`, add inside `<dependencies>`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

- [ ] **Step 2: Add token helper**

In `TokenService.java`, add this public helper below `getLoginUser(HttpServletRequest request)`:

```java
public LoginUser getLoginUser(String token)
{
    if (StringUtils.isNotEmpty(token))
    {
        try
        {
            if (token.startsWith(Constants.TOKEN_PREFIX))
            {
                token = token.replace(Constants.TOKEN_PREFIX, "");
            }
            Claims claims = parseToken(token);
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            return redisCache.getCacheObject(userKey);
        }
        catch (Exception e)
        {
            log.error("获取用户信息异常'{}'", e.getMessage());
        }
    }
    return null;
}
```

- [ ] **Step 3: Permit WebSocket handshake path**

In `SecurityConfig.java`, add `/ws/chat/**` to permit-all request matchers:

```java
.requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/druid/**", "/ws/chat/**").permitAll()
```

The WebSocket handler still rejects connections without a valid token.

- [ ] **Step 4: Create REST controller**

```java
package com.ruoyi.web.controller.creative;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import com.ruoyi.system.service.creative.ICreativeChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/chat")
public class CreativeChatController extends BaseController
{
    @Autowired
    private ICreativeChatService creativeChatService;

    @GetMapping("/session/list")
    public TableDataInfo listSessions()
    {
        startPage();
        List<CreativeChatSession> list = creativeChatService.listMySessions(SecurityUtils.getUserId());
        return getDataTable(list);
    }

    @PostMapping("/session")
    public AjaxResult openSession(@RequestBody CreativeChatSessionRequest request)
    {
        return success(creativeChatService.openSession(request, SecurityUtils.getUserId(), getUsername()));
    }

    @GetMapping("/message/list")
    public AjaxResult listMessages(@RequestParam Long sessionId)
    {
        return success(creativeChatService.listMessages(sessionId, SecurityUtils.getUserId()));
    }

    @PostMapping("/message")
    public AjaxResult sendMessage(@RequestBody CreativeChatMessageRequest request)
    {
        CreativeChatMessage message = creativeChatService.sendMessage(request, SecurityUtils.getUserId(), getUsername());
        return success(message);
    }

    @PostMapping("/session/{sessionId}/read")
    public AjaxResult markRead(@PathVariable Long sessionId)
    {
        return toAjax(creativeChatService.markRead(sessionId, SecurityUtils.getUserId()));
    }
}
```

- [ ] **Step 5: Create WebSocket config and handler**

`CreativeChatWebSocketConfig.java`:

```java
package com.ruoyi.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class CreativeChatWebSocketConfig implements WebSocketConfigurer
{
    @Autowired
    private CreativeChatWebSocketHandler creativeChatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(creativeChatWebSocketHandler, "/ws/chat").setAllowedOriginPatterns("*");
    }
}
```

`CreativeChatWebSocketHandler.java`:

```java
package com.ruoyi.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.service.creative.ICreativeChatService;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CreativeChatWebSocketHandler extends TextWebSocketHandler
{
    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_USERNAME = "username";

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired private TokenService tokenService;
    @Autowired private ICreativeChatService creativeChatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String token = getQueryValue(session.getUri(), "token");
        LoginUser loginUser = tokenService.getLoginUser(token);
        if (loginUser == null || loginUser.getUser() == null)
        {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("invalid token"));
            return;
        }
        Long userId = loginUser.getUserId();
        session.getAttributes().put(ATTR_USER_ID, userId);
        session.getAttributes().put(ATTR_USERNAME, loginUser.getUsername());
        sessions.put(userId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception
    {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        String username = (String) session.getAttributes().get(ATTR_USERNAME);
        CreativeChatMessageRequest request = objectMapper.readValue(textMessage.getPayload(), CreativeChatMessageRequest.class);
        CreativeChatMessage message = creativeChatService.sendMessage(request, userId, username);
        String payload = objectMapper.writeValueAsString(Map.of("type", "message", "data", message));
        sendIfOpen(userId, payload);
        sendIfOpen(message.getReceiverId(), payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (userId != null)
        {
            sessions.remove(userId, session);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception
    {
        session.close(CloseStatus.SERVER_ERROR);
    }

    private void sendIfOpen(Long userId, String payload) throws Exception
    {
        WebSocketSession target = sessions.get(userId);
        if (target != null && target.isOpen())
        {
            target.sendMessage(new TextMessage(payload));
        }
    }

    private String getQueryValue(URI uri, String key)
    {
        if (uri == null || StringUtils.isEmpty(uri.getQuery()))
        {
            return null;
        }
        String prefix = key + "=";
        for (String part : uri.getQuery().split("&"))
        {
            if (part.startsWith(prefix))
            {
                return part.substring(prefix.length());
            }
        }
        return null;
    }
}
```

- [ ] **Step 6: Run backend package**

```powershell
mvn -pl ruoyi-admin -am -DskipTests package
```

Expected: build success.

- [ ] **Step 7: Commit backend transport task**

```powershell
git add ruoyi-admin/pom.xml ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeChatController.java ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketConfig.java ruoyi-admin/src/main/java/com/ruoyi/web/websocket/CreativeChatWebSocketHandler.java
git commit -m "feat(creative): 接入聊天REST和WebSocket [Codex]"
```

---

### Task 5: Frontend Chat API, Socket Wrapper, And Page

**Files:**
- Create: `ruoyi-ui/src/api/creative/chat.js`
- Create: `ruoyi-ui/src/utils/chatSocket.js`
- Create: `ruoyi-ui/src/views/portal/chat.vue`
- Modify: `ruoyi-ui/src/router/index.js`
- Modify: `ruoyi-ui/vue.config.js`

- [ ] **Step 1: Create chat API wrapper**

```js
import request from '@/utils/request'

export function listChatSessions(query) {
  return request({
    url: '/portal/chat/session/list',
    method: 'get',
    params: query
  })
}

export function openChatSession(data) {
  return request({
    url: '/portal/chat/session',
    method: 'post',
    data
  })
}

export function listChatMessages(sessionId) {
  return request({
    url: '/portal/chat/message/list',
    method: 'get',
    params: { sessionId }
  })
}

export function sendChatMessage(data) {
  return request({
    url: '/portal/chat/message',
    method: 'post',
    data
  })
}

export function markChatRead(sessionId) {
  return request({
    url: '/portal/chat/session/' + sessionId + '/read',
    method: 'post'
  })
}
```

- [ ] **Step 2: Create WebSocket wrapper**

```js
import { getToken } from '@/utils/auth'

export function createChatSocket({ onMessage, onOpen, onClose, onError }) {
  let socket = null
  let closedByUser = false
  let reconnectCount = 0

  function buildUrl() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const base = process.env.VUE_APP_BASE_API || ''
    const apiBase = base.startsWith('http') ? base : window.location.origin + base
    const url = new URL(apiBase)
    return protocol + '//' + url.host + '/ws/chat?token=' + encodeURIComponent(getToken())
  }

  function connect() {
    closedByUser = false
    socket = new WebSocket(buildUrl())
    socket.onopen = () => {
      reconnectCount = 0
      if (onOpen) onOpen()
    }
    socket.onmessage = event => {
      if (onMessage) onMessage(JSON.parse(event.data))
    }
    socket.onerror = event => {
      if (onError) onError(event)
    }
    socket.onclose = event => {
      if (onClose) onClose(event)
      if (!closedByUser && reconnectCount < 3) {
        reconnectCount += 1
        window.setTimeout(connect, reconnectCount * 1000)
      }
    }
  }

  function send(data) {
    if (!socket || socket.readyState !== WebSocket.OPEN) {
      return false
    }
    socket.send(JSON.stringify(data))
    return true
  }

  function close() {
    closedByUser = true
    if (socket) socket.close()
  }

  connect()
  return { send, close }
}
```

- [ ] **Step 3: Add route and dev proxy**

In `router/index.js`, add under `/portal` children:

```js
{
  path: 'chat',
  component: () => import('@/views/portal/chat'),
  name: 'PortalChat',
  meta: { title: '在线沟通', icon: 'message' }
}
```

In `vue.config.js`, inside `devServer.proxy`, add websocket support to the existing API proxy or add:

```js
ws: true,
changeOrigin: true
```

Expected final proxy item still points to `target` from `VUE_APP_BASE_API` setup.

- [ ] **Step 4: Create chat page**

Create `chat.vue` with:

```vue
<template>
  <div class="chat-page">
    <aside class="session-panel">
      <div class="panel-title">在线沟通</div>
      <el-empty v-if="!sessions.length && !loading" description="暂无会话" />
      <div
        v-for="item in sessions"
        :key="item.sessionId"
        :class="['session-item', activeSession && activeSession.sessionId === item.sessionId ? 'active' : '']"
        @click="selectSession(item)"
      >
        <div class="session-main">
          <strong>{{ item.peerName || '对方用户' }}</strong>
          <span>{{ item.lastMessageTime || '' }}</span>
        </div>
        <div class="session-sub">
          <span>{{ item.targetName }}</span>
          <el-badge v-if="item.unreadCount" :value="item.unreadCount" />
        </div>
        <p>{{ item.lastMessage || '还没有消息' }}</p>
      </div>
    </aside>

    <section class="chat-panel">
      <template v-if="activeSession">
        <header class="chat-header">
          <div>
            <strong>{{ activeSession.peerName || '在线沟通' }}</strong>
            <span>{{ targetTypeText(activeSession.targetType) }}：{{ activeSession.targetName }}</span>
          </div>
          <el-tag size="mini" :type="socketReady ? 'success' : 'info'">{{ socketReady ? '实时在线' : '连接中' }}</el-tag>
        </header>

        <main ref="messageBody" class="message-body">
          <div
            v-for="message in messages"
            :key="message.messageId || message.createTime + message.content"
            :class="['message-row', message.senderId === currentUserId ? 'mine' : 'peer']"
          >
            <div class="bubble">
              <el-image
                v-if="message.messageType === 'image'"
                class="chat-image"
                :src="message.content"
                :preview-src-list="[message.content]"
                fit="cover"
              />
              <span v-else>{{ message.content }}</span>
            </div>
          </div>
        </main>

        <footer class="input-panel">
          <el-upload
            class="image-upload"
            action="/dev-api/common/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <el-button icon="el-icon-picture-outline" />
          </el-upload>
          <el-input
            v-model="messageText"
            type="textarea"
            :rows="2"
            resize="none"
            placeholder="输入消息"
            @keyup.enter.native.exact="sendText"
          />
          <el-button type="primary" @click="sendText">发送</el-button>
        </footer>
      </template>
      <el-empty v-else description="选择一个会话开始沟通" />
    </section>
  </div>
</template>

<script>
import { getInfo } from '@/api/login'
import { getToken } from '@/utils/auth'
import { createChatSocket } from '@/utils/chatSocket'
import { listChatSessions, listChatMessages, sendChatMessage, markChatRead, openChatSession } from '@/api/creative/chat'

export default {
  name: 'PortalChat',
  data() {
    return {
      loading: false,
      sessions: [],
      activeSession: null,
      messages: [],
      messageText: '',
      socket: null,
      socketReady: false,
      currentUserId: null,
      uploadHeaders: { Authorization: 'Bearer ' + getToken() }
    }
  },
  created() {
    this.bootstrap()
  },
  beforeDestroy() {
    if (this.socket) this.socket.close()
  },
  methods: {
    bootstrap() {
      getInfo().then(res => {
        this.currentUserId = res.user.userId
        return this.ensureRouteSession()
      }).then(() => {
        this.loadSessions()
        this.connectSocket()
      })
    },
    ensureRouteSession() {
      const { targetType, targetId, creatorId } = this.$route.query
      if (!targetType || !targetId) return Promise.resolve()
      return openChatSession({ targetType, targetId: Number(targetId), creatorId: creatorId ? Number(creatorId) : undefined })
        .then(res => {
          this.activeSession = res.data
        })
    },
    loadSessions() {
      this.loading = true
      listChatSessions({ pageNum: 1, pageSize: 100 }).then(res => {
        this.sessions = res.rows || []
        if (!this.activeSession && this.sessions.length) {
          this.activeSession = this.sessions[0]
        }
        if (this.activeSession) {
          const match = this.sessions.find(item => item.sessionId === this.activeSession.sessionId)
          if (match) this.activeSession = match
          this.loadMessages()
        }
      }).finally(() => {
        this.loading = false
      })
    },
    connectSocket() {
      this.socket = createChatSocket({
        onOpen: () => { this.socketReady = true },
        onClose: () => { this.socketReady = false },
        onMessage: payload => {
          if (payload.type === 'message' && this.activeSession && payload.data.sessionId === this.activeSession.sessionId) {
            this.messages.push(payload.data)
            this.scrollBottom()
            markChatRead(this.activeSession.sessionId)
          }
          this.loadSessions()
        }
      })
    },
    selectSession(session) {
      this.activeSession = session
      this.loadMessages()
    },
    loadMessages() {
      listChatMessages(this.activeSession.sessionId).then(res => {
        this.messages = res.data || []
        this.scrollBottom()
        this.loadSessions()
      })
    },
    sendText() {
      const content = this.messageText.trim()
      if (!content) return
      this.sendMessage({ sessionId: this.activeSession.sessionId, messageType: 'text', content })
      this.messageText = ''
    },
    sendMessage(message) {
      if (!this.socket || !this.socket.send(message)) {
        sendChatMessage(message).then(res => {
          this.messages.push(res.data)
          this.scrollBottom()
          this.loadSessions()
        })
      }
    },
    beforeImageUpload(file) {
      const isImage = file.type.indexOf('image/') === 0
      if (!isImage) this.$modal.msgError('只能上传图片')
      return isImage
    },
    handleImageSuccess(res) {
      if (res.code !== 200) {
        this.$modal.msgError(res.msg || '上传失败')
        return
      }
      this.sendMessage({ sessionId: this.activeSession.sessionId, messageType: 'image', content: res.url })
    },
    scrollBottom() {
      this.$nextTick(() => {
        const body = this.$refs.messageBody
        if (body) body.scrollTop = body.scrollHeight
      })
    },
    targetTypeText(type) {
      return ({ product: '商品', demand: '需求', order: '订单' })[type] || '沟通'
    }
  }
}
</script>

<style scoped>
.chat-page { display: grid; grid-template-columns: 320px 1fr; gap: 16px; padding: 20px; min-height: calc(100vh - 84px); background: #f5f7fb; }
.session-panel, .chat-panel { background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden; }
.panel-title { padding: 16px; font-weight: 700; border-bottom: 1px solid #e5e7eb; }
.session-item { padding: 12px 14px; border-bottom: 1px solid #f0f2f5; cursor: pointer; }
.session-item.active { background: #ecf5ff; }
.session-main, .session-sub { display: flex; justify-content: space-between; gap: 8px; }
.session-main span, .session-sub span, .session-item p { color: #6b7280; font-size: 12px; }
.session-item p { margin: 6px 0 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.chat-panel { display: flex; flex-direction: column; min-height: 640px; }
.chat-header { display: flex; align-items: center; justify-content: space-between; padding: 16px; border-bottom: 1px solid #e5e7eb; }
.chat-header span { display: block; margin-top: 4px; color: #6b7280; font-size: 12px; }
.message-body { flex: 1; padding: 18px; overflow-y: auto; background: #f9fafb; }
.message-row { display: flex; margin-bottom: 12px; }
.message-row.mine { justify-content: flex-end; }
.bubble { max-width: 58%; padding: 10px 12px; border-radius: 8px; line-height: 1.6; background: #fff; border: 1px solid #e5e7eb; word-break: break-word; }
.message-row.mine .bubble { color: #fff; background: #297e7b; border-color: #297e7b; }
.chat-image { width: 180px; max-height: 180px; border-radius: 6px; }
.input-panel { display: grid; grid-template-columns: 48px 1fr 88px; gap: 10px; padding: 12px; border-top: 1px solid #e5e7eb; }
@media (max-width: 900px) { .chat-page { grid-template-columns: 1fr; } .chat-panel { min-height: 560px; } }
</style>
```

- [ ] **Step 5: Run frontend build**

```powershell
cd ruoyi-ui
npm.cmd run build:prod
```

Expected: build success.

- [ ] **Step 6: Commit frontend chat page task**

```powershell
git add ruoyi-ui/src/api/creative/chat.js ruoyi-ui/src/utils/chatSocket.js ruoyi-ui/src/views/portal/chat.vue ruoyi-ui/src/router/index.js ruoyi-ui/vue.config.js
git commit -m "feat(creative): 新增前台实时聊天页面 [Codex]"
```

---

### Task 6: Portal Entry Points And Documentation

**Files:**
- Modify: `ruoyi-ui/src/views/portal/products.vue`
- Modify: `ruoyi-ui/src/views/portal/demands.vue`
- Modify: `ruoyi-ui/src/views/portal/orders.vue`
- Modify: `ruoyi-ui/src/views/portal/index.vue`
- Modify: `README.md`
- Modify: `docs/collaboration.md`

- [ ] **Step 1: Add product chat entry**

In `products.vue`, import `openChatSession` or route directly. Add a button near cart/detail actions:

```vue
<el-button size="mini" icon="el-icon-service" @click="contactCreator(item)">联系创作者</el-button>
```

Add method:

```js
contactCreator(product) {
  this.$router.push({
    path: '/portal/chat',
    query: {
      targetType: 'product',
      targetId: product.productId,
      creatorId: product.creatorId
    }
  })
}
```

- [ ] **Step 2: Add demand chat entry**

In `demands.vue`, add a button where demand actions are rendered:

```vue
<el-button size="mini" icon="el-icon-chat-dot-round" @click="contactDemand(row)">在线沟通</el-button>
```

Add method:

```js
contactDemand(row) {
  this.$router.push({
    path: '/portal/chat',
    query: {
      targetType: 'demand',
      targetId: row.demandId,
      creatorId: row.creatorId
    }
  })
}
```

If `row.creatorId` is not available in the current demand list, hide the demand chat button for this iteration and keep product and order chat as the supported demo entry. Do not pass a fake creator ID.

- [ ] **Step 3: Add order chat entry**

In `orders.vue`, add a button in the order operation area:

```vue
<el-button size="mini" icon="el-icon-chat-line-round" @click="contactOrder(row)">联系对方</el-button>
```

Add method:

```js
contactOrder(row) {
  this.$router.push({
    path: '/portal/chat',
    query: {
      targetType: 'order',
      targetId: row.orderId
    }
  })
}
```

When implementing `order` in `CreativeChatServiceImpl`, derive `buyerId`, `sellerId`, and `sourceName` from `CreativeOrder`.

- [ ] **Step 4: Add homepage message entry**

In `portal/index.vue`, add one quick action:

```js
{ title: '在线沟通', path: '/portal/chat', icon: 'el-icon-chat-dot-round' }
```

- [ ] **Step 5: Update docs**

In `README.md`, change the recommendation status to out of current scope and mark online communication as implemented after the code is complete:

```markdown
| 需求报价与沟通 | 已支持实时在线沟通，买家与创作者可发送文字和图片，聊天记录落库 | 已完成 |
| 推荐算法 | 本轮按答辩演示取舍，暂不作为当前实现范围 | 暂不实现 |
```

In `docs/collaboration.md`, change the current phase:

```markdown
**当前阶段**：阶段 5 · 实时在线沟通与演示完善。
```

Add completed record after verification:

```markdown
| 2026-04-28 | 实时在线沟通 | 聊天会话、消息落库、WebSocket 推送、图片消息、前台聊天页 | 后端 package、前端 build | Codex | 推荐算法从当前范围移除，优先补齐在线沟通 |
```

- [ ] **Step 6: Run final verification**

Run backend:

```powershell
mvn -pl ruoyi-admin -am -DskipTests package
```

Run frontend:

```powershell
cd ruoyi-ui
npm.cmd run build:prod
```

Expected: both pass.

- [ ] **Step 7: Manual browser verification**

Start backend and frontend:

```powershell
.\bin\run-admin-jdk17.bat
cd ruoyi-ui
npm.cmd run dev
```

Manual expected result:

- Buyer opens product detail and clicks “联系创作者”.
- Creator logs in another browser session and opens `/portal/chat`.
- Buyer sends text; creator receives it without refresh.
- Creator uploads a picture; buyer receives image bubble without refresh.
- Refresh both pages; history messages remain visible.

- [ ] **Step 8: Commit entry and documentation task**

```powershell
git add ruoyi-ui/src/views/portal/products.vue ruoyi-ui/src/views/portal/demands.vue ruoyi-ui/src/views/portal/orders.vue ruoyi-ui/src/views/portal/index.vue README.md docs/collaboration.md
git commit -m "feat(creative): 接入在线沟通入口并更新文档 [Codex]"
```

---

## Self-Review

- Spec coverage:
  - One-to-one chat is covered by session table, service permissions, REST, WebSocket, and chat page.
  - Text and image messages are covered by message type validation, `/common/upload` usage, and image bubbles.
  - Persisted history is covered by `creative_chat_message` and `listMessages`.
  - Unread counts are covered by session unread fields, increment, clear, and UI badge.
  - Product, demand, and order entries are planned. Product is mandatory in Task 3. Order support is mandatory in Task 6. Demand is supported when a creator participant is available; otherwise the button is hidden to avoid invalid sessions.
- Placeholder scan:
  - No `TBD`, `TODO`, or unspecified “handle later” steps remain.
- Type consistency:
  - Request field names are `targetType`, `targetId`, `creatorId`, `sessionId`, `messageType`, and `content`.
  - Session identifiers are consistently `sessionId`.
  - Message identifiers are consistently `messageId`.

