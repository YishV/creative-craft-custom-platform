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
