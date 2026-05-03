# 文创手作定制交易平台 · 部署与运行手册

> 本文档面向第一次拿到代码、需要把项目跑起来的同学或运维人员。按本文顺序操作即可在本机起一套完整的演示环境。
> 项目路径用 `<项目根目录>` 表示（即包含 `pom.xml`、`ruoyi-ui/`、`sql/` 的那一级目录）。

---

## 1. 项目简介

| 项 | 值 |
|---|---|
| 项目名 | 文创手作定制交易平台（基于 RuoYi 二次开发） |
| 后端 | Spring Boot 4.x + MyBatis + Druid + Sa-Token + Quartz |
| 前端 | Vue 2.6 + Element UI 2.15 + vue-cli 5 |
| 数据库 | MySQL 5.7 / 8.0 |
| 缓存 | Redis 5+ |
| JDK | **17**
| 模块 | `ruoyi-admin`（启动模块）、`ruoyi-framework`、`ruoyi-system`、`ruoyi-common`、`ruoyi-quartz`、`ruoyi-generator`、`ruoyi-ui` |

---

## 2. 环境准备

按下表装好这 5 件东西，再继续。

| 软件 | 版本 | 说明 |
|---|---|---|
| JDK | **17**|
| Maven | 3.6+ |
| Node.js | 16 / 18 / 20（LTS 任一） | `node -v` 验证 |
| MySQL | 5.7+ / 8.0 | 字符集 utf8mb4 |

## 3. 数据库与 Redis 初始化

### 3.1 创建库

进入 MySQL 客户端：

```sql
CREATE DATABASE ruoyi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ruoyi;
```

> 库名 `ruoyi` 和后端配置默认值对应。如要改名见 §4.1。

### 3.2 灌入表结构与初始数据

按顺序执行 `<项目根目录>/sql/install/` 下的 5 个 SQL，详细见 [`sql/README.md`](../sql/README.md)：

```sql
SOURCE <项目根目录>/sql/install/01_framework_base.sql;
SOURCE <项目根目录>/sql/install/02_business_core.sql;
SOURCE <项目根目录>/sql/install/03_social_interaction.sql;
SOURCE <项目根目录>/sql/install/04_system_menus.sql;
SOURCE <项目根目录>/sql/install/05_test_data.sql;   -- 仅开发演示用，生产可跳过
```

执行完应有 4×× 张表（`sys_*` 系统表 + `creative_*` 业务表 + Quartz 表）。

---

## 4. 后端启动

### 4.1 修改数据库连接（必做）

打开 `<项目根目录>/ruoyi-admin/src/main/resources/application-druid.yml`，把主库地址、用户名、密码改成你的：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://<MYSQL 主机>:<MYSQL 端口>/<库名>?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: <数据库用户名>
        password: <数据库密码>
```

### 4.1 生产模式（命令行打包 + 运行 jar）

在 `<项目根目录>` 下执行：

```bash
mvn clean package -DskipTests
```

成功后运行：

```bash
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

加 `--spring.profiles.active=prod` 等启动参数可切换环境。后台跑起来日志输出到控制台或重定向到文件：

```bash
# Linux
nohup java -jar ruoyi-admin/target/ruoyi-admin.jar > app.log 2>&1 &

# Windows PowerShell
Start-Process -FilePath java -ArgumentList "-jar","ruoyi-admin/target/ruoyi-admin.jar" -RedirectStandardOutput app.out.log -RedirectStandardError app.err.log
```

### 4.4 验证

- `http://localhost:8080/` 返回 RuoYi 默认提示即说明后端起来了。
- 看日志中 `DruidDataSource inited` 表示数据库连接 OK；`SensitiveWord DFA 重建完成` 表示敏感词词库加载 OK。

---

## 5. 前端启动

### 5.1 安装依赖

进入前端目录：

```bash
cd <项目根目录>/ruoyi-ui
npm install
```

> Windows 上 npm 命令是 `npm.cmd`，在某些 PowerShell 配置下要写全名。
> 国内网络如果装不动，可换淘宝镜像：`npm config set registry https://registry.npmmirror.com`。

### 5.2 开发模式（推荐答辩演示）

```bash
npm run dev
```

默认监听 **80** 端口（如被占用可 `npm run dev -- --port 8090`）。

启动成功后访问：

| URL | 用途 |
|---|---|
| `http://localhost/` | 后台首页（admin 登录后） |
| `http://localhost/login` | 管理员登录入口 |
| `http://localhost/buyer/login` | 买家登录/注册入口（暖橙色） |
| `http://localhost/creator/login` | 创作者登录/注册入口（青绿色） |

> `dev-api` 代理到 `http://localhost:8080`，后端必须先起来。

## 6. 默认账号与登录入口

执行 `01_framework_base.sql` 后会有两个内置账号；执行 `05_test_data.sql` 会再灌一批演示用户（`creator01-10`、`buyer01-10`，密码统一 `admin123`）。

| 入口 URL | 角色 | 默认账号 | 登录后跳转 |
|---|---|---|---|
| `/login` | 系统管理员 | `admin` / `admin123` | `/index`（后台首页） |
| `/buyer/login` | 买家 | 注册即可，或用 `  ` ~ `buyer10` / `admin123` | `/portal/index`（前台首页） |
| `/creator/login` | 创作者/卖家 | 注册即可，或用 ` ` ~ `creator10` / `admin123` | `/creative/creator/me`（我的店铺） |

> 三个登录页底部都有"切换其他入口"链接，互通无障碍。
> 买家/卖家用户的侧边栏只看得到文创业务菜单，**看不到任何系统管理/系统监控/系统工具菜单**，权限由 `sys_role_menu` 表关联控制。
