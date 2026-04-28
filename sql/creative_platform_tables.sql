-- 文创手作定制交易平台基础业务表

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
  product_id bigint(20) not null auto_increment comment '商品ID',
  creator_id bigint(20) default null comment '创作者ID',
  category_id bigint(20) default null comment '分类ID',
  product_name varchar(128) not null comment '商品名称',
  product_type varchar(32) default 'spot' comment '商品类型',
  price decimal(10,2) default 0.00 comment '售价',
  status char(1) default '0' comment '状态',
  remark varchar(500) default null comment '备注',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default current_timestamp comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
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
