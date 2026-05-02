-- 修复创作者越权 (后端配套):
--   1) 撤销 creator 角色 "创作者管理" 菜单 (2001/2100/2101/2102) — 已在 creator_role_revoke_admin_menu_20260502.sql 处理
--   2) 撤销 creator 角色 "分类管理" 菜单 (2002) — 平台级分类由 admin 维护
--   作品分享/评论互动 的数据隔离改为后端切面 @CreativeDataScope 自动过滤,无需 SQL 配合
-- 影响表: sys_role_menu
DELETE FROM sys_role_menu
WHERE role_id = 101
  AND menu_id IN (2002);
