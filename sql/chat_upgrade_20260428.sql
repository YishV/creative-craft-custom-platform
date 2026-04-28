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
