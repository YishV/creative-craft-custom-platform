-- ----------------------------
-- 商品/评论审核流升级补丁 (2026-05-01)
-- ----------------------------
-- 用途：为已经执行过 02 / 03 / 04 安装脚本的旧库追加：
--   1) creative_product 增加审核相关列（默认 pending）
--   2) 旧库历史商品全部回填为 approved，避免演示时已有商品因审核状态而无法上架
--   3) 商品审核 / 评论审核按钮权限菜单（2114-2117）
-- 新库安装应同步合并到 02 / 04 安装脚本，无需执行此文件。
-- ----------------------------

-- 1. creative_product 审核列
alter table creative_product
  add column audit_status varchar(32)  default 'pending' comment '审核状态(pending/approved/rejected)' after status,
  add column audit_remark varchar(500) default null     comment '审核备注'                                   after audit_status,
  add column audit_by     varchar(64)  default ''       comment '审核人'                                     after audit_remark,
  add column audit_time   datetime     default null     comment '审核时间'                                   after audit_by;

-- 2. 旧库存量商品回填为已通过，避免影响现有演示数据
update creative_product set audit_status = 'approved' where audit_status is null or audit_status = '';

-- 3. 按钮权限菜单
delete from sys_menu_role where menu_id in (2114, 2115, 2116, 2117);
delete from sys_menu      where menu_id in (2114, 2115, 2116, 2117);

insert into sys_menu values ('2114', '商品审核', '2003', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:audit', '#', 'admin', sysdate(), '', null, '商品上架前审核');
insert into sys_menu values ('2115', '评论审核', '2008', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:audit', '#', 'admin', sysdate(), '', null, '评论审核');
