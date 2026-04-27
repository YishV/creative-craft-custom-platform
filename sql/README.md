# SQL 执行顺序说明

> 这份说明只解决一个问题：数据库初始化时，先执行什么，后执行什么。

---

## 1. 文件作用

| 文件 | 类型 | 作用 | 是否建议重复执行 |
|---|---|---|---|
| `ry_20260417.sql` | 基础库 | 若依系统表、系统菜单、默认用户、默认角色 | 不建议，会重建系统表 |
| `quartz.sql` | 基础库 | 定时任务 Quartz 表 | 不建议，会重建 Quartz 表 |
| `creative_platform_tables.sql` | 业务库 | 文创业务表：创作者、分类、商品、需求、报价、订单、作品、评论、收藏 | 不建议，会重建业务表 |
| `creative_platform_menu.sql` | 菜单 | 文创后台菜单与按钮权限 | 可重复执行，但会删除并重建 2000-2149 菜单 |
| `creative_creator_audit_upgrade_20260425.sql` | 增量 | 给创作者表补审核字段，初始化 creator 角色 | 可重复执行 |
| `buyer_role_upgrade_20260425.sql` | 增量 | 初始化 buyer 角色，并给历史 common 用户补 buyer | 可重复执行 |
| `creator_me_menu_20260426.sql` | 增量 | 新增创作者个人中心菜单 2150，并授权 creator | 可重复执行 |
| `register_enable_20260427.sql` | 增量 | 开启若依自助注册开关 | 可重复执行 |

---

## 2. 新数据库执行顺序

适用于空数据库，或者你明确要重新初始化全部表。

```sql
source sql/ry_20260417.sql;
source sql/quartz.sql;
source sql/creative_platform_tables.sql;
source sql/creative_platform_menu.sql;
source sql/creative_creator_audit_upgrade_20260425.sql;
source sql/buyer_role_upgrade_20260425.sql;
source sql/creator_me_menu_20260426.sql;
source sql/register_enable_20260427.sql;
```

说明：

- `ry_20260417.sql` 必须最先执行，因为后面的菜单、角色、权限都依赖 `sys_*` 表。
- `quartz.sql` 和业务表没有强依赖，但属于若依运行基础表，建议放在基础库之后。
- `creative_platform_tables.sql` 创建文创业务表。
- `creative_platform_menu.sql` 依赖 `sys_menu`，所以必须在若依基础 SQL 后执行。
- 三个增量脚本放在最后，用来补角色、审核字段、个人中心菜单。

---

## 3. 旧库升级顺序

适用于已经执行过基础 SQL 和早期文创 SQL 的数据库。

```sql
source sql/creative_creator_audit_upgrade_20260425.sql;
source sql/buyer_role_upgrade_20260425.sql;
source sql/creator_me_menu_20260426.sql;
source sql/register_enable_20260427.sql;
```

如果后台菜单异常，再补执行：

```sql
source sql/creative_platform_menu.sql;
```

注意：

- 不要在有数据的旧库上随便重新执行 `ry_20260417.sql`，它会 `drop table`。
- 不要在有业务数据的旧库上随便重新执行 `creative_platform_tables.sql`，它也会 `drop table`。
- `creative_creator_audit_upgrade_20260425.sql` 已做字段存在性检查，可用于旧库升级，也可放在新库初始化末尾补 `creator` 角色。

---

## 4. 推荐导入方式

进入 MySQL 后执行：

```sql
create database ruoyi default character set utf8mb4 collate utf8mb4_general_ci;
use ruoyi;

source D:/workspace/sihuo/RuoYi-Vue-master/sql/ry_20260417.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/quartz.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/creative_platform_tables.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/creative_platform_menu.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/creative_creator_audit_upgrade_20260425.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/buyer_role_upgrade_20260425.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/creator_me_menu_20260426.sql;
source D:/workspace/sihuo/RuoYi-Vue-master/sql/register_enable_20260427.sql;
```

如果你在 MySQL 命令行里已经切到项目根目录，也可以使用相对路径：

```sql
source sql/ry_20260417.sql;
```

---

## 5. 导入后检查

```sql
show tables like 'creative_%';

select role_id, role_name, role_key
from sys_role
where role_key in ('admin', 'common', 'buyer', 'creator');

select menu_id, menu_name, perms
from sys_menu
where menu_id between 2000 and 2150
order by menu_id;
```

能看到文创业务表、`buyer` / `creator` 角色、2000-2150 菜单，才算初始化基本完成。
