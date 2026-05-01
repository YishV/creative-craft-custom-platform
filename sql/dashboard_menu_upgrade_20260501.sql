-- ----------------------------
-- 数据看板菜单升级补丁 (2026-05-01)
-- ----------------------------
-- 用途：为已经执行过 04_system_menus.sql 的旧库追加"数据看板"顶级菜单。
-- 新库安装无需执行此文件，04_system_menus.sql 已经包含相同内容。
-- ----------------------------

delete from sys_menu where menu_id = 2050;

-- 顶级菜单：数据看板（管理员可见，sort=0 置顶以便答辩演示）
insert into sys_menu values ('2050', '数据看板', '0', '0', 'creative-dashboard', 'creative/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'creative:dashboard:view', 'chart', 'admin', sysdate(), '', null, '文创平台数据看板');

-- 默认不分配给买家(100)/卖家(101)角色，仅 admin（拥有 *:*:*) 可见。
-- 如需对其他角色开放，自行 insert into sys_role_menu(role_id, menu_id) values (?, 2050);
