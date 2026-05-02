-- ----------------------------
-- 1. 文创平台基础菜单 (从 ID 2000 开始)
-- ----------------------------
delete from sys_menu where menu_id between 2000 and 2149;

-- 目录
insert into sys_menu values ('2000', '文创平台', '0', '5', 'creative', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '文创平台目录');
-- 子菜单
insert into sys_menu values ('2001', '创作者管理', '2000', '1', 'creator', 'creative/creator/index', '', '', 1, 0, 'C', '0', '0', 'creative:creator:list', 'user', 'admin', sysdate(), '', null, '创作者管理菜单');
insert into sys_menu values ('2002', '分类管理', '2000', '2', 'category', 'creative/category/index', '', '', 1, 0, 'C', '0', '0', 'creative:category:list', 'tree', 'admin', sysdate(), '', null, '分类管理菜单');
insert into sys_menu values ('2003', '商品管理', '2000', '3', 'product', 'creative/product/index', '', '', 1, 0, 'C', '0', '0', 'creative:product:list', 'build', 'admin', sysdate(), '', null, '商品管理菜单');
insert into sys_menu values ('2004', '定制需求', '2000', '4', 'demand', 'creative/demand/index', '', '', 1, 0, 'C', '0', '0', 'creative:demand:list', 'form', 'admin', sysdate(), '', null, '定制需求菜单');
insert into sys_menu values ('2005', '定制报价', '2000', '5', 'quote', 'creative/quote/index', '', '', 1, 0, 'C', '0', '0', 'creative:quote:list', 'edit', 'admin', sysdate(), '', null, '定制报价菜单');
insert into sys_menu values ('2006', '定制订单', '2000', '6', 'order', 'creative/order/index', '', '', 1, 0, 'C', '0', '0', 'creative:order:list', 'shopping', 'admin', sysdate(), '', null, '定制订单菜单');
insert into sys_menu values ('2007', '作品分享', '2000', '7', 'post', 'creative/post/index', '', '', 1, 0, 'C', '0', '0', 'creative:post:list', 'post', 'admin', sysdate(), '', null, '作品分享菜单');
insert into sys_menu values ('2008', '评论互动', '2000', '8', 'comment', 'creative/comment/index', '', '', 1, 0, 'C', '0', '0', 'creative:comment:list', 'message', 'admin', sysdate(), '', null, '评论互动菜单');
insert into sys_menu values ('2009', '收藏关注', '2000', '9', 'favorite', 'creative/favorite/index', '', '', 1, 0, 'C', '0', '0', 'creative:favorite:list', 'star', 'admin', sysdate(), '', null, '收藏关注菜单');
insert into sys_menu values ('2010', '消息中心', '2000', '10', 'chat', 'creative/chat/index', '', '', 1, 0, 'C', '0', '0', 'creative:chat:list', 'message', 'admin', sysdate(), '', null, '实时聊天菜单');
insert into sys_menu values ('2011', '敏感词库', '2000', '11', 'sensitive', 'creative/sensitive/index', '', '', 1, 0, 'C', '0', '0', 'creative:sensitive:list', 'eye-open', 'admin', sysdate(), '', null, '敏感词词库管理');

-- 顶级菜单：数据看板（管理员可见，sort=0 置顶以便答辩演示）
insert into sys_menu values ('2050', '数据看板', '0', '0', 'creative-dashboard', 'creative/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'creative:dashboard:view', 'chart', 'admin', sysdate(), '', null, '文创平台数据看板');

-- 按钮权限 (省略部分，仅展示核心)
insert into sys_menu values ('2100', '创作者查询', '2001', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2110', '敏感词查询', '2011', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2111', '敏感词新增', '2011', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2112', '敏感词修改', '2011', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2113', '敏感词删除', '2011', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:sensitive:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2114', '商品审核', '2003', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:audit', '#', 'admin', sysdate(), '', null, '商品上架前审核');
insert into sys_menu values ('2115', '评论审核', '2008', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:audit', '#', 'admin', sysdate(), '', null, '评论审核');
-- ... (其他按钮权限保持不变)

-- ----------------------------
-- 2. 初始化部门（仅两类：买家 / 卖家）
-- ----------------------------
INSERT IGNORE INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time)
VALUES (200, 0, '0', '买家', 1, '系统', '', '', '0', '0', 'admin', sysdate());

INSERT IGNORE INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time)
VALUES (201, 0, '0', '卖家', 2, '系统', '', '', '0', '0', 'admin', sysdate());

-- ----------------------------
-- 3. 初始化角色 (权限隔离)
-- ----------------------------
-- 创建买家角色
INSERT IGNORE INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time)
VALUES (100, '普通买家', 'buyer', 2, '1', '0', '0', 'admin', sysdate());

-- 创建卖家角色（role_key='creator' 与 CreativeCreatorServiceImpl.CREATOR_ROLE_KEY 对齐）
INSERT IGNORE INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, del_flag, create_by, create_time)
VALUES (101, '创作者/卖家', 'creator', 3, '1', '0', '0', 'admin', sysdate());

-- ----------------------------
-- 4. 建立角色与菜单关联
-- ----------------------------
-- 设计原则：买家/卖家角色只关联 2000~2999 的文创业务菜单，
--          不关联 menu_id=1/2/3/4 等系统管理/监控/工具/项目说明菜单，
--          即可保证登录后侧边栏看不到任何系统管理类信息。
-- 系统级菜单仅 user_id=1 的 admin 因 SecurityUtils.isAdmin 直接全量返回。
DELETE FROM sys_role_menu WHERE role_id IN (100, 101);

-- 买家：文创平台目录、消息、定制需求、定制订单、作品分享、收藏关注
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(100, 2000), (100, 2010), (100, 2004), (100, 2006), (100, 2007), (100, 2009);

-- 卖家：文创平台目录、消息、创作者管理、商品管理、定制需求（接单视角）、定制报价、定制订单、作品分享、评论互动
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(101, 2000), (101, 2010), (101, 2001), (101, 2003), (101, 2004), (101, 2005), (101, 2006), (101, 2007), (101, 2008);
