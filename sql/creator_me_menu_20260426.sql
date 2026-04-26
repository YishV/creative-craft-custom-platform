-- 阶段 3 · 创作者主页（个人中心）菜单与权限
-- 增量执行，可重复运行；与 creative_platform_menu.sql 中已用区间 [2000-2149] 兼容（避开占用区段）。

-- 1) 主页菜单：放在文创平台目录 (parent=2000) 下，路由 me，组件 creative/me/index
delete from sys_menu where menu_id = 2150;
insert into sys_menu values
('2150', '创作者主页', '2000', '0', 'me', 'creative/me/index', '', '', 1, 0, 'C', '0', '0', 'creative:creator:me', 'people', 'admin', sysdate(), '', null, '创作者个人中心 — 档案与统计');

-- 2) 角色绑定：creator 角色默认拥有该菜单
--    通过 role_key 找到 role_id，再写入 sys_role_menu。
insert into sys_role_menu(role_id, menu_id)
select r.role_id, 2150 from sys_role r
where r.role_key = 'creator'
  and not exists (select 1 from sys_role_menu rm where rm.role_id = r.role_id and rm.menu_id = 2150);

-- 3) 顺带把目录(2000) + 创作者管理(2001/查询权限 2100)的查看权限也补给 creator，
--    方便创作者本人提交申请、查看自己档案审核状态（数据权限层已限制访问范围）。
insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join (select 2000 as menu_id union all select 2001 union all select 2100) m
  on r.role_key = 'creator'
where not exists (
  select 1 from sys_role_menu rm where rm.role_id = r.role_id and rm.menu_id = m.menu_id
);
