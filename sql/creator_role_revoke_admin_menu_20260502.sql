-- 修复：creator 角色不应拥有"创作者管理"管理员菜单（越权可改/审核别人档案）
-- 影响：role_id=101 (creator/卖家)
-- 处理：撤掉 menu_id 2001(创作者管理菜单) 及其子按钮 2100/2101/2102
-- 创作者本人档案维护走 /creative/creator/me（getMyProfile 按当前 userId 取）
DELETE FROM sys_role_menu
WHERE role_id = 101
  AND menu_id IN (2001, 2100, 2101, 2102);
