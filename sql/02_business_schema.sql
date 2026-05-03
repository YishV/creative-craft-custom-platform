-- ============================================================
-- 02 业务表结构 + 菜单角色权限
-- 来源: install/02_business_core.sql + install/03_social_interaction.sql + install/04_system_menus.sql
-- ============================================================

-- ----------------------------
-- 1. 业务基础表
-- ----------------------------

drop table if exists creative_creator;
create table creative_creator (
  creator_id bigint(20) not null auto_increment comment '创作者ID',
  user_id bigint(20) default null comment '关联用户ID',
  creator_name varchar(64) not null comment '创作者名称',
  store_name varchar(128) default null comment '店铺名称',
  creator_level varchar(32) default 'normal' comment '创作者等级',
  status char(1) default '0' comment '状态（0正常 1停用）',
  audit_status varchar(32) default 'pending' comment '审核状态（pending待审核 approved通过 rejected驳回）',
  audit_remark varchar(500) default null comment '审核备注',
  audit_by varchar(64) default '' comment '审核人',
  audit_time datetime default null comment '审核时间',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  primary key (creator_id)
) engine=innodb comment='创作者表';

drop table if exists creative_category;
create table creative_category (
  category_id bigint(20) not null auto_increment comment '分类ID',
  parent_id bigint(20) default 0 comment '父分类ID',
  category_name varchar(64) not null comment '分类名称',
  category_code varchar(64) default null comment '分类编码',
  sort_num int(11) default 0 comment '排序值',
  status char(1) default '0' comment '状态',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  primary key (category_id)
) engine=innodb comment='文创分类表';

drop table if exists creative_product;
create table creative_product (
  product_id    bigint(20)   not null auto_increment comment '商品ID',
  creator_id    bigint(20)   default null comment '创作者ID',
  category_id   bigint(20)   default null comment '分类ID',
  product_name  varchar(128) not null comment '商品名称',
  product_type  varchar(32)  default 'spot' comment '商品类型',
  price         decimal(10,2) default 0.00 comment '售价',
  status        char(1)      default '1' comment '状态：0上架 1下架',
  audit_status  varchar(32)  default 'pending' comment '审核状态：pending/approved/rejected',
  audit_remark  varchar(500) default null comment '审核备注',
  audit_by      varchar(64)  default '' comment '审核人',
  audit_time    datetime     default null comment '审核时间',
  remark        varchar(500) default null comment '备注',
  create_by     varchar(64)  default '' comment '创建者',
  create_time   datetime     default current_timestamp comment '创建时间',
  update_by     varchar(64)  default '' comment '更新者',
  update_time   datetime     default null comment '更新时间',
  primary key (product_id)
) engine=innodb comment='手作商品表';

drop table if exists creative_demand;
create table creative_demand (
  demand_id bigint(20) not null auto_increment comment '需求ID',
  user_id bigint(20) default null comment '买家ID',
  category_id bigint(20) default null comment '分类ID',
  demand_title varchar(128) not null comment '需求标题',
  budget_amount decimal(10,2) default 0.00 comment '预算金额',
  demand_status varchar(32) default 'draft' comment '需求状态',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  primary key (demand_id)
) engine=innodb comment='定制需求表';

drop table if exists creative_quote;
create table creative_quote (
  quote_id bigint(20) not null auto_increment comment '报价ID',
  demand_id bigint(20) default null comment '需求ID',
  creator_id bigint(20) default null comment '创作者ID',
  quote_amount decimal(10,2) default 0.00 comment '报价金额',
  delivery_days int(11) default 7 comment '交付天数',
  quote_status varchar(32) default 'pending' comment '报价状态',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  primary key (quote_id)
) engine=innodb comment='定制报价表';

drop table if exists creative_custom_order;
create table creative_custom_order (
  order_id bigint(20) not null auto_increment comment '订单ID',
  order_no varchar(64) not null comment '订单编号',
  buyer_id bigint(20) default null comment '买家ID',
  seller_id bigint(20) default null comment '卖家ID',
  order_amount decimal(10,2) default 0.00 comment '订单金额',
  order_status varchar(32) default 'created' comment '订单状态',
  pay_status varchar(32) default 'unpaid' comment '支付状态（unpaid未支付 paid已支付）',
  source_type varchar(32) default null comment '订单来源类型',
  source_id bigint(20) default null comment '订单来源ID',
  source_name varchar(128) default null comment '订单来源名称',
  quantity int(11) default 1 comment '购买数量',
  address_snapshot varchar(500) default null comment '收货信息快照',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  primary key (order_id)
) engine=innodb comment='定制订单表';


-- ---------- install/03_social_interaction.sql ----------

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

-- ---------- install/04_system_menus.sql ----------

-- ----------------------------
-- 1. 文创平台基础菜单 (从 ID 2000 开始)
-- ----------------------------
delete from sys_menu where menu_id between 2000 and 2149;

-- 目录
insert into sys_menu values ('2000', '文创平台', '0', '5', 'creative', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '文创平台目录');
-- 子菜单
insert into sys_menu values ('2001', '创作者管理', '2000', '1', 'creator', 'creative/creator/index', '', '', 1, 0, 'C', '0', '0', 'creative:creator:list', 'user', 'admin', sysdate(), '', null, '创作者管理菜单');
insert into sys_menu values ('2002', '分类管理', '2000', '2', 'category', 'creative/category/index', '', '', 1, 0, 'C', '0', '0', 'creative:category:list', 'tree', 'admin', sysdate(), '', null, '分类管理菜单');
insert into sys_menu values ('2003', '商品管理', '2000', '3', 'product', 'creative/product/index', '', '', 1, 0, 'C', '0', '0', 'creative:product:list', 'build', 'admin', sysdate(), '', null, '商品管理菜单');
insert into sys_menu values ('2004', '定制需求', '2000', '4', 'demand', 'creative/demand/index', '', '', 1, 0, 'C', '0', '0', 'creative:demand:list', 'form', 'admin', sysdate(), '', null, '定制需求菜单');
insert into sys_menu values ('2005', '定制报价', '2000', '5', 'quote', 'creative/quote/index', '', '', 1, 0, 'C', '0', '0', 'creative:quote:list', 'edit', 'admin', sysdate(), '', null, '定制报价菜单');
insert into sys_menu values ('2006', '定制订单', '2000', '6', 'order', 'creative/order/index', '', '', 1, 0, 'C', '0', '0', 'creative:order:list', 'shopping', 'admin', sysdate(), '', null, '定制订单菜单');
insert into sys_menu values ('2007', '作品分享', '2000', '7', 'post', 'creative/post/index', '', '', 1, 0, 'C', '0', '0', 'creative:post:list', 'post', 'admin', sysdate(), '', null, '作品分享菜单');
insert into sys_menu values ('2008', '评论互动', '2000', '8', 'comment', 'creative/comment/index', '', '', 1, 0, 'C', '0', '0', 'creative:comment:list', 'message', 'admin', sysdate(), '', null, '评论互动菜单');
insert into sys_menu values ('2009', '收藏关注', '2000', '9', 'favorite', 'creative/favorite/index', '', '', 1, 0, 'C', '0', '0', 'creative:favorite:list', 'star', 'admin', sysdate(), '', null, '收藏关注菜单');
insert into sys_menu values ('2010', '消息中心', '2000', '10', 'chat', 'creative/chat/index', '', '', 1, 0, 'C', '0', '0', 'creative:chat:list', 'message', 'admin', sysdate(), '', null, '实时聊天菜单');
insert into sys_menu values ('2011', '敏感词库', '2000', '11', 'sensitive', 'creative/sensitive/index', '', '', 1, 0, 'C', '0', '0', 'creative:sensitive:list', 'eye-open', 'admin', sysdate(), '', null, '敏感词词库管理');

-- 顶级菜单：数据看板（管理员可见，sort=0 置顶以便答辩演示）
insert into sys_menu values ('2050', '数据看板', '0', '0', 'creative-dashboard', 'creative/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'creative:dashboard:view', 'chart', 'admin', sysdate(), '', null, '文创平台数据看板');

-- 按钮权限 (省略部分，仅展示核心)
insert into sys_menu values ('2100', '创作者查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2110', '敏感词查询', '2011', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2111', '敏感词新增', '2011', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2112', '敏感词修改', '2011', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2113', '敏感词删除', '2011', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2114', '商品审核', '2003', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:audit', '#', 'admin', sysdate(), '', null, '商品上架前审核');
insert into sys_menu values ('2115', '评论审核', '2008', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:audit', '#', 'admin', sysdate(), '', null, '评论审核');
insert into sys_menu values ('2101', '创作者新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2102', '创作者修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2103', '创作者删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2120', '分类查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:category:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2121', '商品查询', '2003', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2122', '商品新增', '2003', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2123', '商品修改', '2003', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2124', '商品删除', '2003', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2125', '需求查询', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2126', '需求新增', '2004', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2127', '需求修改', '2004', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2128', '需求删除', '2004', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2129', '报价查询', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2130', '报价新增', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2131', '报价修改', '2005', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2132', '报价删除', '2005', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2133', '订单查询', '2006', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2134', '订单新增', '2006', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2135', '订单修改', '2006', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2136', '订单删除', '2006', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2137', '作品查询', '2007', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2138', '作品新增', '2007', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2139', '作品修改', '2007', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2140', '作品删除', '2007', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2141', '评论查询', '2008', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2142', '评论新增', '2008', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2143', '评论修改', '2008', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2144', '评论删除', '2008', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2145', '收藏查询', '2009', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2146', '收藏新增', '2009', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2147', '收藏修改', '2009', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2148', '收藏删除', '2009', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:remove', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 2. 初始化部门（仅两类：买家 / 卖家）
-- ----------------------------
INSERT IGNORE INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time)
VALUES (200, 0, '0', '买家', 1, '系统', '', '', '0', '0', 'admin', sysdate());

INSERT IGNORE INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time)
VALUES (201, 0, '0', '卖家', 2, '系统', '', '', '0', '0', 'admin', sysdate());

-- ----------------------------
-- 3. 初始化角色 (权限隔离)
-- ----------------------------
-- 创建买家角色
INSERT IGNORE INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time)
VALUES (100, '普通买家', 'buyer', 2, '1', '0', '0', 'admin', sysdate());

-- 创建卖家角色（role_key='creator' 与 CreativeCreatorServiceImpl.CREATOR_ROLE_KEY 对齐）
INSERT IGNORE INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time)
VALUES (101, '创作者/卖家', 'creator', 3, '1', '0', '0', 'admin', sysdate());

-- ----------------------------
-- 4. 建立角色与菜单关联
-- ----------------------------
-- 设计原则：买家/卖家角色只关联 2000~2999 的文创业务菜单，
--          不关联 menu_id=1/2/3/4 等系统管理/监控/工具/项目说明菜单，
--          即可保证登录后侧边栏看不到任何系统管理类信息。
-- 系统级菜单仅 user_id=1 的 admin 因 SecurityUtils.isAdmin 直接全量返回。
DELETE FROM sys_role_menu WHERE role_id IN (100, 101);

-- 买家：文创平台目录、消息、定制需求、定制订单、作品分享、收藏关注
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(100, 2000), (100, 2010), (100, 2004), (100, 2006), (100, 2007), (100, 2009),
(100, 2125), (100, 2126), (100, 2127), (100, 2128),
(100, 2129), (100, 2131),
(100, 2133), (100, 2135),
(100, 2137),
(100, 2145), (100, 2146), (100, 2148);

-- 卖家：文创平台目录、消息、商品管理、定制需求（接单视角）、定制报价、定制订单、作品分享、评论互动、收藏关注
-- 注意：
--   1) 不挂"创作者管理"(2001)：管理员审核创作者申请的页面，创作者本人走 /creative/creator/me；
--   2) 不挂"分类管理"(2002)：分类是平台级数据，admin 维护，creator 只在新建商品时下拉选；保留 2120(分类查询)按钮权限即可；
--   3) 作品分享/评论互动/收藏关注 通过 @CreativeDataScope 自动按当前 creatorId / userId 过滤，creator 只能看自己的数据。
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(101, 2000), (101, 2010), (101, 2003), (101, 2004), (101, 2005), (101, 2006), (101, 2007), (101, 2008), (101, 2009),
(101, 2120),
(101, 2121), (101, 2122), (101, 2123), (101, 2124),
(101, 2125),
(101, 2129), (101, 2130), (101, 2131), (101, 2132),
(101, 2133), (101, 2135),
(101, 2137), (101, 2138), (101, 2139), (101, 2140),
(101, 2141), (101, 2142), (101, 2143), (101, 2144),
(101, 2145), (101, 2146), (101, 2147), (101, 2148);


