-- 隐藏默认框架外链与公告内容
-- 用途：旧库升级时去掉侧边栏默认外链，并替换系统公告里的默认框架信息。

update sys_menu
set menu_name = '项目说明',
    path = 'about',
    is_frame = 1,
    visible = '1',
    icon = 'documentation',
    remark = '项目说明菜单，默认隐藏',
    update_by = 'admin',
    update_time = sysdate()
where menu_id = 4;

update sys_notice
set notice_title = '文创平台演示说明',
    notice_content = '<p>文创手作定制交易平台用于毕业设计演示，核心流程包括用户注册登录、商品浏览、定制需求发布、报价沟通、购物车结算、模拟支付、订单流转、社区作品与收藏关注。</p>',
    update_by = 'admin',
    update_time = sysdate()
where notice_id = 3;

update sys_notice
set notice_title = '文创平台演示提示',
    notice_content = '请按 注册登录 -> 浏览商品 -> 加入购物车 -> 结算支付 -> 订单中心 的顺序演示买家流程。',
    update_by = 'admin',
    update_time = sysdate()
where notice_id = 1;

update sys_notice
set notice_title = '创作者演示提示',
    notice_content = '创作者可维护档案、发布商品、查看需求并提交报价，报价被买家选中后进入订单制作流程。',
    update_by = 'admin',
    update_time = sysdate()
where notice_id = 2;
