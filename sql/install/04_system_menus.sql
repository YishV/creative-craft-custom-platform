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
insert into sys_menu values ('2101', '创作者新增', '2001', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2102', '创作者修改', '2001', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2103', '创作者删除', '2001', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:creator:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2120', '分类查询', '2002', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:category:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2121', '商品查询', '2003', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2122', '商品新增', '2003', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2123', '商品修改', '2003', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2124', '商品删除', '2003', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:product:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2125', '需求查询', '2004', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2126', '需求新增', '2004', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2127', '需求修改', '2004', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2128', '需求删除', '2004', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:demand:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2129', '报价查询', '2005', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2130', '报价新增', '2005', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2131', '报价修改', '2005', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2132', '报价删除', '2005', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:quote:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2133', '订单查询', '2006', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2134', '订单新增', '2006', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2135', '订单修改', '2006', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2136', '订单删除', '2006', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:order:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2137', '作品查询', '2007', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2138', '作品新增', '2007', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2139', '作品修改', '2007', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2140', '作品删除', '2007', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:post:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2141', '评论查询', '2008', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2142', '评论新增', '2008', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2143', '评论修改', '2008', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2144', '评论删除', '2008', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:comment:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2145', '收藏查询', '2009', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2146', '收藏新增', '2009', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2147', '收藏修改', '2009', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2148', '收藏删除', '2009', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'creative:favorite:remove', '#', 'admin', sysdate(), '', null, '');

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
(100, 2000), (100, 2010), (100, 2004), (100, 2006), (100, 2007), (100, 2009),
(100, 2125), (100, 2126), (100, 2127), (100, 2128),
(100, 2129), (100, 2131),
(100, 2133), (100, 2135),
(100, 2137),
(100, 2145), (100, 2146), (100, 2148);

-- 卖家：文创平台目录、消息、创作者管理、商品管理、定制需求（接单视角）、定制报价、定制订单、作品分享、评论互动
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(101, 2000), (101, 2010), (101, 2001), (101, 2002), (101, 2003), (101, 2004), (101, 2005), (101, 2006), (101, 2007), (101, 2008), (101, 2009),
(101, 2100), (101, 2101), (101, 2102),
(101, 2120),
(101, 2121), (101, 2122), (101, 2123), (101, 2124),
(101, 2125),
(101, 2129), (101, 2130), (101, 2131), (101, 2132),
(101, 2133), (101, 2135),
(101, 2137), (101, 2138), (101, 2139), (101, 2140),
(101, 2141), (101, 2142), (101, 2143), (101, 2144),
(101, 2145), (101, 2146), (101, 2147), (101, 2148);
