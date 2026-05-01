-- ----------------------------
-- 文创平台演示数据脚本
-- ----------------------------

-- 清理数据（可选，生产环境请勿执行）
-- truncate table creative_interaction_share;
-- truncate table creative_interaction_follow;
-- truncate table creative_interaction_like;
-- truncate table creative_interaction_comment;
-- truncate table creative_comment;
-- truncate table creative_post;
-- truncate table creative_custom_order;
-- truncate table creative_quote;
-- truncate table creative_demand;
-- truncate table creative_product;
-- truncate table creative_creator;
-- truncate table creative_category;

-- 1. 分类数据
INSERT INTO creative_category (category_id, parent_id, category_name, category_code, sort_num, status, create_by, create_time) VALUES
(1, 0, '木艺手工', 'wood', 1, '0', 'admin', sysdate()),
(2, 0, '陶艺雕塑', 'ceramic', 2, '0', 'admin', sysdate()),
(3, 0, '织物刺绣', 'textile', 3, '0', 'admin', sysdate()),
(4, 0, '金属首饰', 'jewelry', 4, '0', 'admin', sysdate()),
(5, 0, '皮具手作', 'leather', 5, '0', 'admin', sysdate()),
(6, 1, '木雕摆件', 'wood_statue', 1, '0', 'admin', sysdate()),
(7, 2, '青花瓷器', 'blue_white', 1, '0', 'admin', sysdate()),
(8, 3, '苏绣配饰', 'suzhou_embroidery', 1, '0', 'admin', sysdate()),
(9, 4, '银饰定制', 'silver_jewelry', 1, '0', 'admin', sysdate()),
(10, 5, '手缝皮包', 'handmade_leather_bag', 1, '0', 'admin', sysdate());

-- 2. 创作者数据（user_id 对应演示用户，可按实际账号调整）
INSERT INTO creative_creator (creator_id, user_id, creator_name, store_name, creator_level, status, audit_status, audit_by, audit_time, create_by, create_time) VALUES
(1, 1, '天工开物', '天工木艺坊', 'master', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(2, 2, '泥火之歌', '景德镇陶艺社', 'master', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(3, 3, '锦线慢作', '苏绣生活馆', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(4, 4, '银月工坊', '银月首饰定制', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(5, 5, '一针一线', '手作布艺研究所', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(6, 6, '皮革先生', '慢裁皮具', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(7, 7, '竹影手造', '竹影器物', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(8, 8, '瓦蓝陶舍', '瓦蓝陶舍', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(9, 9, '流光珐琅', '流光珐琅社', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate()),
(10, 10, '纸上花开', '纸艺花园', 'normal', '0', 'approved', 'admin', sysdate(), 'admin', sysdate());

-- 3. 商品数据
INSERT INTO creative_product (product_id, creator_id, category_id, product_name, product_type, price, status, audit_status, audit_by, audit_time, remark, create_by, create_time) VALUES
(1, 1, 6, '手工紫檀木梳', 'spot', 299.00, '0', 'approved', 'admin', sysdate(), '细齿打磨，适合日常使用和礼赠。', 'admin', sysdate()),
(2, 2, 7, '手绘青花瓷瓶', 'spot', 1200.00, '0', 'approved', 'admin', sysdate(), '景德镇高温烧制，釉色稳定。', 'admin', sysdate()),
(3, 3, 8, '苏绣梅花胸针', 'spot', 168.00, '0', 'approved', 'admin', sysdate(), '真丝线手绣，配礼盒。', 'admin', sysdate()),
(4, 4, 9, '手作银杏叶项链', 'spot', 389.00, '0', 'approved', 'admin', sysdate(), '银饰手工锤纹，可刻字。', 'admin', sysdate()),
(5, 5, 3, '拼布托特包', 'spot', 219.00, '0', 'approved', 'admin', sysdate(), '棉麻拼布，内置小口袋。', 'admin', sysdate()),
(6, 6, 10, '手缝植鞣革钱包', 'spot', 499.00, '0', 'approved', 'admin', sysdate(), '意大利植鞣革，手工缝线。', 'admin', sysdate()),
(7, 7, 1, '竹编茶席收纳篮', 'spot', 259.00, '0', 'approved', 'admin', sysdate(), '老竹篾编织，适合茶空间。', 'admin', sysdate()),
(8, 8, 2, '柴烧马克杯', 'spot', 188.00, '0', 'approved', 'admin', sysdate(), '每只釉面纹理略有差异。', 'admin', sysdate()),
(9, 9, 4, '珐琅彩耳环', 'spot', 328.00, '0', 'approved', 'admin', sysdate(), '低温珐琅上色，925 银针。', 'admin', sysdate()),
(10, 10, 3, '纸艺永生花灯', 'custom', 459.00, '0', 'approved', 'admin', sysdate(), '可定制花色与灯光色温。', 'admin', sysdate());

-- 4. 定制需求数据
INSERT INTO creative_demand (demand_id, user_id, category_id, demand_title, budget_amount, demand_status, remark, create_by, create_time) VALUES
(1, 11, 6, '定制一件胡桃木桌面摆件', 800.00, 'published', '希望加入山水纹理，适合办公室。', 'buyer01', sysdate()),
(2, 12, 7, '婚礼伴手礼陶瓷小杯 20 套', 2600.00, 'quoting', '需要统一包装和姓名贴。', 'buyer02', sysdate()),
(3, 13, 8, '企业文化刺绣徽章', 1500.00, 'selected', '用于团队活动，每枚约 5cm。', 'buyer03', sysdate()),
(4, 14, 9, '纪念日银戒一对', 1200.00, 'published', '需要内圈刻字。', 'buyer04', sysdate()),
(5, 15, 10, '通勤皮革双肩包', 1800.00, 'quoting', '需要电脑隔层和防水处理。', 'buyer05', sysdate()),
(6, 16, 3, '亲子手工布偶', 600.00, 'draft', '预算可调整，先保存草稿。', 'buyer06', sysdate()),
(7, 17, 1, '茶室竹编灯罩', 900.00, 'published', '直径约 35cm，暖色灯光。', 'buyer07', sysdate()),
(8, 18, 2, '民宿房间陶艺门牌', 1300.00, 'quoting', '需要 12 个不同编号。', 'buyer08', sysdate()),
(9, 19, 4, '珐琅胸针企业礼品', 3000.00, 'selected', '首批 30 枚，后续可能复购。', 'buyer09', sysdate()),
(10, 20, 3, '纸艺橱窗装置', 5000.00, 'closed', '预算审批未通过，先关闭。', 'buyer10', sysdate());

-- 5. 报价数据
INSERT INTO creative_quote (quote_id, demand_id, creator_id, quote_amount, delivery_days, quote_status, remark, create_by, create_time) VALUES
(1, 1, 1, 760.00, 12, 'pending', '可提供 2 版草图确认。', 'admin', sysdate()),
(2, 2, 2, 2480.00, 18, 'pending', '含独立礼盒包装。', 'admin', sysdate()),
(3, 3, 3, 1450.00, 15, 'selected', '已确认企业色和针法。', 'admin', sysdate()),
(4, 4, 4, 1180.00, 10, 'pending', '支持 6 字以内刻字。', 'admin', sysdate()),
(5, 5, 6, 1720.00, 25, 'pending', '含一次结构调整。', 'admin', sysdate()),
(6, 6, 5, 580.00, 8, 'rejected', '买家暂缓制作。', 'admin', sysdate()),
(7, 7, 7, 880.00, 14, 'pending', '可配合灯具尺寸调整。', 'admin', sysdate()),
(8, 8, 8, 1260.00, 16, 'pending', '门牌可加釉下彩编号。', 'admin', sysdate()),
(9, 9, 9, 2880.00, 20, 'selected', '已确认礼品交付排期。', 'admin', sysdate()),
(10, 10, 10, 4800.00, 30, 'rejected', '需求已关闭。', 'admin', sysdate());

-- 6. 订单数据（包含商品订单与报价生成订单）
INSERT INTO creative_custom_order (order_id, order_no, buyer_id, seller_id, order_amount, order_status, pay_status, source_type, source_id, source_name, quantity, address_snapshot, remark, create_by, create_time) VALUES
(1, 'CRAFT202605010001', 11, 1, 299.00, 'created', 'unpaid', 'product', 1, '手工紫檀木梳', 1, '张三 13800000001 浙江杭州西湖区文三路 1 号', '待支付商品订单', 'buyer01', sysdate()),
(2, 'CRAFT202605010002', 12, 2, 2400.00, 'making', 'paid', 'product', 2, '手绘青花瓷瓶', 2, '李四 13800000002 江西景德镇珠山区陶溪川', '制作中的商品订单', 'buyer02', sysdate()),
(3, 'CRAFT202605010003', 13, 3, 1450.00, 'shipped', 'paid', 'quote', 3, '企业文化刺绣徽章', 1, '王五 13800000003 上海浦东新区张江路 8 号', '报价转订单，已发货', 'buyer03', sysdate()),
(4, 'CRAFT202605010004', 14, 4, 389.00, 'finished', 'paid', 'product', 4, '手作银杏叶项链', 1, '赵六 13800000004 江苏苏州姑苏区平江路', '已完成商品订单', 'buyer04', sysdate()),
(5, 'CRAFT202605010005', 15, 6, 1720.00, 'created', 'unpaid', 'quote', 5, '通勤皮革双肩包', 1, '钱七 13800000005 广东深圳南山区科技园', '报价转订单待支付', 'buyer05', sysdate()),
(6, 'CRAFT202605010006', 16, 5, 219.00, 'cancelled', 'unpaid', 'product', 5, '拼布托特包', 1, '孙八 13800000006 四川成都锦江区春熙路', '买家取消订单', 'buyer06', sysdate()),
(7, 'CRAFT202605010007', 17, 7, 259.00, 'making', 'paid', 'product', 7, '竹编茶席收纳篮', 1, '周九 13800000007 福建福州鼓楼区八一七路', '制作中商品订单', 'buyer07', sysdate()),
(8, 'CRAFT202605010008', 18, 8, 376.00, 'finished', 'paid', 'product', 8, '柴烧马克杯', 2, '吴十 13800000008 浙江宁波海曙区鼓楼', '已完成商品订单', 'buyer08', sysdate()),
(9, 'CRAFT202605010009', 19, 9, 2880.00, 'making', 'paid', 'quote', 9, '珐琅胸针企业礼品', 1, '郑十一 13800000009 北京朝阳区望京', '企业礼品制作中', 'buyer09', sysdate()),
(10, 'CRAFT202605010010', 20, 10, 459.00, 'shipped', 'paid', 'product', 10, '纸艺永生花灯', 1, '冯十二 13800000010 重庆渝中区解放碑', '已发货商品订单', 'buyer10', sysdate());

-- 7. 作品分享数据
INSERT INTO creative_post (post_id, creator_id, post_title, post_type, status, remark, create_by, create_time) VALUES
(1, 1, '记录一次紫檀木雕刻全过程', 'work', '0', '从选料到抛光的完整记录。', 'admin', sysdate()),
(2, 2, '新出窑的一批青花作品展示', 'work', '0', '展示釉色和器型差异。', 'admin', sysdate()),
(3, 3, '梅花胸针的针法拆解', 'tutorial', '0', '适合刺绣初学者参考。', 'admin', sysdate()),
(4, 4, '银杏叶项链锤纹制作', 'work', '0', '记录金属肌理形成过程。', 'admin', sysdate()),
(5, 5, '拼布色彩搭配思路', 'tutorial', '0', '介绍布料色块组合。', 'admin', sysdate()),
(6, 6, '植鞣革钱包养护说明', 'article', '0', '讲解日常护理方式。', 'admin', sysdate()),
(7, 7, '竹编收纳篮的收口技巧', 'tutorial', '0', '重点展示边缘收口。', 'admin', sysdate()),
(8, 8, '柴烧杯釉面自然落灰', 'work', '0', '展示不可复制的柴烧肌理。', 'admin', sysdate()),
(9, 9, '珐琅彩耳环上色过程', 'work', '0', '多层上色和烧结记录。', 'admin', sysdate()),
(10, 10, '纸艺花灯结构设计', 'article', '0', '介绍纸艺灯具骨架。', 'admin', sysdate());

-- 8. 旧版作品评论数据
INSERT INTO creative_comment (comment_id, post_id, user_id, comment_content, audit_status, create_by, create_time) VALUES
(1, 1, 11, '老师的手艺太赞了！', 'approved', 'admin', sysdate()),
(2, 1, 12, '请问这种木材哪里可以买到？', 'approved', 'admin', sysdate()),
(3, 2, 13, '青花釉色很稳，想看更多细节图。', 'approved', 'admin', sysdate()),
(4, 3, 14, '针法说明很清楚。', 'approved', 'admin', sysdate()),
(5, 4, 15, '锤纹效果很自然。', 'approved', 'admin', sysdate()),
(6, 5, 16, '配色很适合春夏。', 'approved', 'admin', sysdate()),
(7, 6, 17, '钱包养护说明很实用。', 'approved', 'admin', sysdate()),
(8, 7, 18, '竹编收口看起来很结实。', 'approved', 'admin', sysdate()),
(9, 8, 19, '柴烧纹理很特别。', 'approved', 'admin', sysdate()),
(10, 9, 20, '珐琅色彩很亮。', 'approved', 'admin', sysdate());

-- 9. 新版通用社区交互评论
INSERT INTO creative_interaction_comment (target_type, target_id, user_id, user_name, avatar, content, parent_id, score, status, gmt_create) VALUES
('post', 1, 11, '木作爱好者', '', '非常详细的教程，适合收藏。', 0, 5, '0', sysdate()),
('product', 1, 12, '礼物采购员', '', '实物比照片还漂亮，好评。', 0, 5, '0', sysdate()),
('post', 2, 13, '陶艺学生', '', '想了解烧制温度和时间。', 0, 5, '0', sysdate()),
('product', 3, 14, '刺绣收藏者', '', '胸针很精致，包装也稳妥。', 0, 5, '0', sysdate()),
('product', 4, 15, '首饰控', '', '项链刻字效果不错。', 0, 5, '0', sysdate()),
('post', 5, 16, '布艺新手', '', '配色方案很有参考价值。', 0, 5, '0', sysdate()),
('product', 6, 17, '通勤用户', '', '钱包手感很好，走线整齐。', 0, 5, '0', sysdate()),
('post', 7, 18, '茶室主理人', '', '竹编作品很适合茶空间。', 0, 5, '0', sysdate()),
('product', 8, 19, '杯具收藏者', '', '杯型舒服，釉面有层次。', 0, 5, '0', sysdate()),
('post', 10, 20, '纸艺粉丝', '', '花灯结构很巧妙。', 0, 5, '0', sysdate());
