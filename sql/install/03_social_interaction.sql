    drop table if exists creative_post;
    create table creative_post (
      post_id bigint(20) not null auto_increment comment '作品ID',
      creator_id bigint(20) default null comment '创作者ID',
      post_title varchar(128) not null comment '作品标题',
      post_type varchar(32) default 'work' comment '内容类型',
      status char(1) default '0' comment '状态',
      remark varchar(500) default null comment '备注',
      create_by varchar(64) default '' comment '创建者',
      create_time datetime default current_timestamp comment '创建时间',
      update_by varchar(64) default '' comment '更新者',
      update_time datetime default null comment '更新时间',
      primary key (post_id)
    ) engine=innodb comment='作品分享表';

    drop table if exists creative_comment;
    create table creative_comment (
      comment_id bigint(20) not null auto_increment comment '评论ID',
      post_id bigint(20) default null comment '作品ID',
      user_id bigint(20) default null comment '评论用户ID',
      comment_content varchar(500) default null comment '评论内容',
      audit_status varchar(32) default 'pending' comment '审核状态',
      remark varchar(500) default null comment '备注',
      create_by varchar(64) default '' comment '创建者',
      create_time datetime default current_timestamp comment '创建时间',
      update_by varchar(64) default '' comment '更新者',
      update_time datetime default null comment '更新时间',
      primary key (comment_id)
    ) engine=innodb comment='评论互动表';

    -- 敏感词词库（动态敏感词过滤模块，2026-05-01）
    drop table if exists creative_sensitive_word;
    create table creative_sensitive_word (
      word_id     bigint(20)   not null auto_increment comment '词ID',
      word        varchar(64)  not null comment '敏感词',
      category    varchar(32)  default 'general' comment '分类：general/politics/abuse/ad 等',
      severity    char(1)      default '1' comment '等级：1低 2中 3高',
      status      char(1)      default '0' comment '状态：0启用 1停用',
      remark      varchar(200) default null comment '备注',
      create_by   varchar(64)  default '' comment '创建者',
      create_time datetime     default current_timestamp comment '创建时间',
      update_by   varchar(64)  default '' comment '更新者',
      update_time datetime     default null comment '更新时间',
      primary key (word_id),
      unique key uk_word (word)
    ) engine=innodb comment='敏感词词库';

    insert into creative_sensitive_word (word, category, severity, status, create_by) values
    ('傻逼',     'abuse',    '2', '0', 'admin'),
    ('混蛋',     'abuse',    '1', '0', 'admin'),
    ('滚蛋',     'abuse',    '1', '0', 'admin'),
    ('草泥马',   'abuse',    '3', '0', 'admin'),
    ('法轮',     'politics', '3', '0', 'admin'),
    ('赌博',     'general',  '2', '0', 'admin'),
    ('代开发票', 'ad',       '2', '0', 'admin'),
    ('微信加我', 'ad',       '1', '0', 'admin');

    drop table if exists creative_favorite_follow;
    create table creative_favorite_follow (
      favorite_id bigint(20) not null auto_increment comment '关系ID',
      user_id bigint(20) default null comment '用户ID',
      target_type varchar(32) default 'creator' comment '目标类型',
      target_id bigint(20) default null comment '目标ID',
      status char(1) default '0' comment '状态',
      remark varchar(500) default null comment '备注',
      create_by varchar(64) default '' comment '创建者',
      create_time datetime default current_timestamp comment '创建时间',
      update_by varchar(64) default '' comment '更新者',
      update_time datetime default null comment '更新时间',
      primary key (favorite_id)
    ) engine=innodb comment='收藏关注表';

    -- Source: chat_upgrade_20260428.sql

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

    -- Source: community_interaction_upgrade_20260428.sql

    -- ----------------------------
    -- 1. 评论/评价表
    -- ----------------------------
    create table if not exists creative_interaction_comment (
      comment_id    bigint not null auto_increment comment '评论ID',
      target_type   varchar(20) not null comment '目标类型(product-商品, post-社区帖子)',
      target_id     bigint not null comment '目标ID',
      user_id       bigint not null comment '评论人ID',
      user_name     varchar(50) comment '评论人昵称',
      avatar        varchar(255) comment '评论人头像',
      content       text not null comment '评论内容',
      parent_id     bigint default 0 comment '父评论ID',
      score         tinyint default 5 comment '评分(1-5星)',
      status        char(1) default '0' comment '状态(0显示 1隐藏)',
      gmt_create    datetime default current_timestamp comment '创建时间',
      primary key (comment_id),
      key idx_interaction_comment_target (target_type, target_id),
      key idx_interaction_comment_user (user_id)
    ) engine=innodb comment = '社区交互-评论表';

    -- ----------------------------
    -- 2. 点赞表
    -- ----------------------------
    create table if not exists creative_interaction_like (
      like_id       bigint not null auto_increment comment '点赞ID',
      target_type   varchar(20) not null comment '目标类型(product, post, comment)',
      target_id     bigint not null comment '目标ID',
      user_id       bigint not null comment '用户ID',
      gmt_create    datetime default current_timestamp comment '点赞时间',
      primary key (like_id),
      unique key uk_interaction_like (target_type, target_id, user_id)
    ) engine=innodb comment = '社区交互-点赞表';

    -- ----------------------------
    -- 3. 关注表
    -- ----------------------------
    create table if not exists creative_interaction_follow (
      follow_id     bigint not null auto_increment comment '关注ID',
      creator_id    bigint not null comment '创作者ID',
      user_id       bigint not null comment '关注者ID',
      gmt_create    datetime default current_timestamp comment '关注时间',
      primary key (follow_id),
      unique key uk_interaction_follow (creator_id, user_id)
    ) engine=innodb comment = '社区交互-关注表';

    -- ----------------------------
    -- 4. 分享统计表 (简单记录)
    -- ----------------------------
    create table if not exists creative_interaction_share (
      share_id      bigint not null auto_increment comment '分享ID',
      target_type   varchar(20) not null comment '目标类型',
      target_id     bigint not null comment '目标ID',
      user_id       bigint comment '分享人ID',
      platform      varchar(20) comment '分享平台(wechat, weibo, link)',
      gmt_create    datetime default current_timestamp comment '分享时间',
      primary key (share_id)
    ) engine=innodb comment = '社区交互-分享记录表';

    -- Source: hide_ruoyi_brand_upgrade_20260428.sql

    -- 隐藏默认框架外链与公告内容
    -- 用途：旧库升级时去掉侧边栏默认外链，并替换系统公告里的默认框架信息。

    update sys_menu
    set menu_name = '项目说明',
        path = 'about',
        is_frame = 1,
        visible = '1',
        icon = 'documentation',
        remark = '项目说明菜单，默认隐藏',
        update_by = 'admin',
        update_time = sysdate()
    where menu_id = 4;

    update sys_notice
    set notice_title = '文创平台演示说明',
        notice_content = '<p>文创手作定制交易平台用于毕业设计演示，核心流程包括用户注册登录、商品浏览、定制需求发布、报价沟通、购物车结算、模拟支付、订单流转、社区作品与收藏关注。</p>',
        update_by = 'admin',
        update_time = sysdate()
    where notice_id = 3;

    update sys_notice
    set notice_title = '文创平台演示提示',
        notice_content = '请按 注册登录 -> 浏览商品 -> 加入购物车 -> 结算支付 -> 订单中心 的顺序演示买家流程。',
        update_by = 'admin',
        update_time = sysdate()
    where notice_id = 1;

    update sys_notice
    set notice_title = '创作者演示提示',
        notice_content = '创作者可维护档案、发布商品、查看需求并提交报价，报价被买家选中后进入订单制作流程。',
        update_by = 'admin',
        update_time = sysdate()
    where notice_id = 2;
