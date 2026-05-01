# 数据库安装指南 · 文创手作定制交易平台

本目录下所有 SQL 都按"先全量、再补丁"的两层结构组织。先看下面这张图判断你属于哪种场景，再按章节对号执行。

```
你在哪一步？
├── 全新数据库 ─────────────► 走 §1，按 01→05 顺序执行
├── 旧库已有数据，想保留 ───► 走 §2，按需追加 sql/*_upgrade_*.sql 补丁
└── 旧库太老不想兼容了 ─────► 重导：drop database + 走 §1
```

---

## §1 · 全新数据库（推荐）

进入 MySQL 后按编号顺序执行 `sql/install/` 下的 5 个文件：

| 顺序 | 文件 | 内容 | 必选 |
|---|---|---|---|
| 1 | `01_framework_base.sql` | 若依基础表 + Quartz 定时任务 + 注册开关 | ✅ |
| 2 | `02_business_core.sql`  | 创作者 / 分类 / 商品（含审核字段）/ 需求 / 报价 / 订单 / 支付 | ✅ |
| 3 | `03_social_interaction.sql` | 作品 / 评论（双轨制）/ 互动 / 聊天 / 敏感词词库 + 初始 8 条 | ✅ |
| 4 | `04_system_menus.sql`   | 后台菜单 + 角色 + 权限（含数据看板、敏感词、审核） | ✅ |
| 5 | `05_test_data.sql`      | 演示数据（创作者 / 商品 / 作品 / 评论） | ⛔ 仅开发演示 |

```sql
source sql/install/01_framework_base.sql;
source sql/install/02_business_core.sql;
source sql/install/03_social_interaction.sql;
source sql/install/04_system_menus.sql;
source sql/install/05_test_data.sql;       -- 可选
```

执行完登录后台需要刷新页面或重新登录才能看到新菜单。

---

## §2 · 旧库升级补丁

只想给已有库加新功能、不想重导，就在 `sql/` 根目录下按下面顺序执行对应补丁：

| 日期 | 补丁 | 用途 | 影响表 |
|---|---|---|---|
| 2026-05-01 | `dashboard_menu_upgrade_20260501.sql`       | 数据看板顶级菜单 | `sys_menu` |
| 2026-05-01 | `sensitive_word_upgrade_20260501.sql`       | 敏感词词库表 + 词库后台菜单 + 初始 8 条 | `creative_sensitive_word`、`sys_menu` |
| 2026-05-01 | `product_comment_audit_upgrade_20260501.sql`| 商品审核字段 + 商品/评论审核按钮权限；存量商品自动回填 approved | `creative_product`、`sys_menu` |

每个补丁内部都是幂等的（`drop ... if exists` / `delete ... where id in`），重跑无副作用。新库已合并到 §1 的安装脚本，**不要重复执行**。

---

## §3 · 表与模块对照

按答辩讲解时分组方便随时翻：

| 模块 | 主要表 |
|---|---|
| 用户与权限   | `sys_user` `sys_role` `sys_menu` `sys_role_menu` `sys_user_role` |
| 创作者档案   | `creative_creator`（含审核字段） |
| 商品交易     | `creative_category` `creative_product`（含审核字段）`creative_demand` `creative_quote` `creative_custom_order` |
| 社区互动     | `creative_post` `creative_comment` `creative_interaction_comment` `creative_interaction_like` `creative_interaction_follow` `creative_favorite_follow` |
| 实时沟通     | `creative_chat_session` `creative_chat_message` |
| 内容治理     | `creative_sensitive_word` |

---

## §4 · 双轨评论说明

历史上有两张评论表：

- `creative_comment` — 早期版本，仅承载作品评论，带 `audit_status`，后台审核流走这张。
- `creative_interaction_comment` — 通用版本，前台社区评论统一走这张，按 `target_type / target_id` 区分商品/作品。

Java 层 `CreativeComment` 实体同时映射两张表，按调用入口选择 Mapper。短期内不打算合并，前台演示用通用表，后台审核演示用旧表。

---

## §5 · 常见坑

- **连不上数据库** → 先看 `ruoyi-admin/src/main/resources/application-druid.yml` 的 `url`，默认端口 3306、库名 `ry-vue`。
- **菜单看不到** → 浏览器强刷或退出重登；新菜单需要重新拉权限缓存。
- **演示数据冲掉了真实数据** → `05_test_data.sql` 的 INSERT 没用 `INSERT IGNORE`，生产库别跑。
- **执行 02 报 audit_status 列已存在** → 你不该跑 02，应该跑 §2 的补丁；新库才跑 §1。
