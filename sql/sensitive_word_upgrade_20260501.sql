-- ----------------------------
-- 动态敏感词过滤模块升级补丁 (2026-05-01)
-- ----------------------------
-- 用途：为已经执行过 03_social_interaction.sql / 04_system_menus.sql 的旧库追加
--   敏感词词库表 + 后台菜单 + 初始词库示例数据。
-- 新库安装请合并到 03 / 04 / 05 安装脚本，无需执行此文件。
-- ----------------------------

-- 1. 词库表
drop table if exists creative_sensitive_word;
create table creative_sensitive_word (
  word_id      bigint(20)   not null auto_increment comment '词ID',
  word         varchar(64)  not null comment '敏感词',
  category     varchar(32)  default 'general' comment '分类：general/politics/abuse/ad 等',
  severity     char(1)      default '1' comment '等级：1低 2中 3高',
  status       char(1)      default '0' comment '状态：0启用 1停用',
  remark       varchar(200) default null comment '备注',
  create_by    varchar(64)  default '' comment '创建者',
  create_time  datetime     default current_timestamp comment '创建时间',
  update_by    varchar(64)  default '' comment '更新者',
  update_time  datetime     default null comment '更新时间',
  primary key (word_id),
  unique key uk_word (word)
) engine=innodb comment='敏感词词库';

-- 2. 初始词库（少量示例，演示用）
insert into creative_sensitive_word (word, category, severity, status, create_by) values
('傻逼',     'abuse',    '2', '0', 'admin'),
('混蛋',     'abuse',    '1', '0', 'admin'),
('滚蛋',     'abuse',    '1', '0', 'admin'),
('草泥马',   'abuse',    '3', '0', 'admin'),
('法轮',     'politics', '3', '0', 'admin'),
('赌博',     'general',  '2', '0', 'admin'),
('代开发票', 'ad',       '2', '0', 'admin'),
('微信加我', 'ad',       '1', '0', 'admin');

-- 3. 后台菜单 + 按钮权限（父级 2000 文创平台目录）
delete from sys_menu_role where menu_id in (2011, 2110, 2111, 2112, 2113);
delete from sys_menu where menu_id in (2011, 2110, 2111, 2112, 2113);

insert into sys_menu values ('2011', '敏感词库', '2000', '11', 'sensitive', 'creative/sensitive/index', '', '', 1, 0, 'C', '0', '0', 'creative:sensitive:list', 'eye-open', 'admin', sysdate(), '', null, '敏感词词库管理');
insert into sys_menu values ('2110', '敏感词查询', '2011', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2111', '敏感词新增', '2011', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2112', '敏感词修改', '2011', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2113', '敏感词删除', '2011', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:remove', '#', 'admin', sysdate(), '', null, '');

-- 默认仅 admin（拥有 *:*:*）可见
