-- ----------------------------
-- 文创平台测试数据脚本 (test_data_seed.sql)
-- ----------------------------

-- 1. 清理数据（可选，生产环境请勿执行）
-- truncate table creative_category;
-- truncate table creative_creator;
-- truncate table creative_product;
-- truncate table creative_post;
-- truncate table creative_comment;
-- truncate table creative_interaction_comment;

-- 2. 插入分类数据
INSERT INTO creative_category (category_id, parent_id, category_name, category_code, sort_num, status, create_by, create_time) VALUES 
(1, 0, '木艺手工', 'wood', 1, '0', 'admin', sysdate()),
(2, 0, '陶艺雕塑', 'ceramic', 2, '0', 'admin', sysdate()),
(3, 1, '木雕摆件', 'wood_statue', 1, '0', 'admin', sysdate()),
(4, 2, '青花瓷器', 'blue_white', 1, '0', 'admin', sysdate());

-- 3. 插入创作者数据 (假设用户ID为1和2)
INSERT INTO creative_creator (creator_id, user_id, creator_name, store_name, creator_level, status, audit_status, create_by, create_time) VALUES 
(1, 1, '天工开物', '天工木艺坊', 'master', '0', 'approved', 'admin', sysdate()),
(2, 2, '泥火之歌', '景德镇陶艺社', 'normal', '0', 'approved', 'admin', sysdate());

-- 4. 插入商品数据
INSERT INTO creative_product (product_id, creator_id, category_id, product_name, product_type, price, status, create_by, create_time) VALUES 
(1, 1, 3, '手工紫檀木梳', 'spot', 299.00, '0', 'admin', sysdate()),
(2, 2, 4, '手绘青花瓷瓶', 'spot', 1200.00, '0', 'admin', sysdate());

-- 5. 插入作品分享数据
INSERT INTO creative_post (post_id, creator_id, post_title, post_type, status, create_by, create_time) VALUES 
(1, 1, '记录一次紫檀木雕刻全过程', 'work', '0', 'admin', sysdate()),
(2, 2, '新出窑的一批作品展示', 'work', '0', 'admin', sysdate());

-- 6. 插入评论数据 (旧版/作品特定：creative_comment)
INSERT INTO creative_comment (post_id, user_id, comment_content, audit_status, create_by, create_time) VALUES 
(1, 1, '老师的手艺太赞了！', 'approved', 'admin', sysdate()),
(1, 2, '请问这种木材哪里可以买到？', 'approved', 'admin', sysdate());

-- 7. 插入通用社区交互评论 (新版：creative_interaction_comment)
INSERT INTO creative_interaction_comment (target_type, target_id, user_id, user_name, avatar, content, parent_id, score, status, gmt_create) VALUES 
('post', 1, 1, '若依管理员', '', '非常详细的教程', 0, 5, '0', sysdate()),
('product', 1, 2, '测试用户', '', '实物比照片还漂亮，好评！', 0, 5, '0', sysdate());
