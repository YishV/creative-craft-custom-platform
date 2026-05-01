-- ----------------------------
-- 文创平台菜单图标修复补丁
-- 日期：2026-05-01
-- 说明：
--   修复旧库中 sys_menu 使用了前端不存在的 image/comment 图标，
--   导致作品分享、评论互动菜单可能不显示图标的问题。
-- ----------------------------

update sys_menu
set icon = 'post'
where menu_id = 2007
  and menu_name = '作品分享';

update sys_menu
set icon = 'message'
where menu_id = 2008
  and menu_name = '评论互动';
