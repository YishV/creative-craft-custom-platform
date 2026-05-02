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
| JDK | **17**（必须，源码 target=17，低版本编译不通过） |
| 模块 | `ruoyi-admin`（启动模块）、`ruoyi-framework`、`ruoyi-system`、`ruoyi-common`、`ruoyi-quartz`、`ruoyi-generator`、`ruoyi-ui` |

---

## 2. 环境准备

按下表装好这 5 件东西，再继续。

| 软件 | 版本 | 说明 |
|---|---|---|
| JDK | **17**（推荐 Temurin / Zulu / GraalVM 17） | 装完确保 `JAVA_HOME` 指向 JDK 17 安装目录，`java -version` 输出 17.x |
| Maven | 3.6+ | `mvn -v` 输出的 Java version 必须是 17 |
| Node.js | 16 / 18 / 20（LTS 任一） | `node -v` 验证 |
| MySQL | 5.7+ / 8.0 | 字符集 utf8mb4 |
| Redis | 5+ | 默认 `localhost:6379` 无密码（项目默认配置） |

### 2.1 JAVA_HOME 配置（最容易踩的坑）

如果 `mvn -v` 显示 Java version 不是 17，编译会报 `无效的目标发布: 17`。两种修法：

**Windows**（PowerShell 当前会话临时设置）：

```powershell
$env:JAVA_HOME = "<JDK17 安装目录>"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
mvn -v
```

永久生效：系统属性 → 环境变量 → 编辑 `JAVA_HOME` 指向 JDK 17 安装目录。

**Linux / macOS**（bash/zsh 临时设置）：

```bash
export JAVA_HOME=<JDK17 安装目录>
export PATH=$JAVA_HOME/bin:$PATH
mvn -v
```

永久生效：写入 `~/.bashrc` 或 `~/.zshrc`。

---

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

### 3.3 启动 Redis

确保本机 Redis 在 `localhost:6379` 监听。Spring Session 与登录态依赖 Redis，**Redis 不起，后端启动会失败**。

```bash
# Linux/macOS
redis-server &

# Windows（用官方版或者 Memurai/WSL）
# 自行启动 redis-server.exe
```

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

如果要改 Redis 地址/密码，编辑 `application.yml` 的 `spring.data.redis.*`。

### 4.2 开发模式（IDE 直接启动）

用 IntelliJ IDEA 打开 `<项目根目录>` 后：

1. **Project SDK / Module SDK / Maven Runner JRE** 三处都选 JDK 17（`File → Project Structure`、`Settings → Maven → Runner`）。
2. 第一次 IDEA 右上角 Maven 面板点刷新，等所有依赖下完。
3. 找到 `ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`，右键 Run。
4. 控制台看到 `Started RuoYiApplication in X.X seconds` 即成功。默认监听 **8080** 端口。

### 4.3 生产模式（命令行打包 + 运行 jar）

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

### 5.3 生产模式（构建 dist）

```bash
npm run build:prod
```

产物在 `<项目根目录>/ruoyi-ui/dist/`，整个目录可以丢到 Nginx / IIS 等静态服务器。

#### Nginx 部署示例

```nginx
server {
    listen 80;
    server_name <你的域名或 IP>;

    location / {
        root  <dist 解压路径>;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://<后端服务地址>:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

部署生产环境时记得改 `ruoyi-ui/.env.production` 里的 `VUE_APP_BASE_API` 与 Nginx `location` 一致。

---

## 6. 默认账号与登录入口

执行 `01_framework_base.sql` 后会有两个内置账号；执行 `05_test_data.sql` 会再灌一批演示用户（`creator01-10`、`buyer01-10`，密码统一 `admin123`）。

| 入口 URL | 角色 | 默认账号 | 登录后跳转 |
|---|---|---|---|
| `/login` | 系统管理员 | `admin` / `admin123` | `/index`（后台首页） |
| `/buyer/login` | 买家 | 注册即可，或用 `buyer01` ~ `buyer10` / `admin123` | `/portal/index`（前台首页） |
| `/creator/login` | 创作者/卖家 | 注册即可，或用 `creator01` ~ `creator10` / `admin123` | `/creative/creator/me`（我的店铺） |

> 三个登录页底部都有"切换其他入口"链接，互通无障碍。
> 买家/卖家用户的侧边栏只看得到文创业务菜单，**看不到任何系统管理/系统监控/系统工具菜单**，权限由 `sys_role_menu` 表关联控制。

---

## 7. 常见问题（FAQ）

### Q1: 编译报 `无效的目标发布: 17`

A: Maven 用的不是 JDK 17。回到 §2.1 配 `JAVA_HOME`，再 `mvn -v` 确认输出是 17。

### Q2: 启动报 `Communications link failure` / `Unknown database`

A: §3 数据库没准备好，或 §4.1 的连接信息不对。检查 MySQL 是否启动、库是否创建、连接 URL 用户名密码是否正确。

### Q3: 启动报 `Unable to connect to Redis`

A: §3.3 的 Redis 没起。本机起一下 `redis-server`。

### Q4: 前端 npm install 卡住

A: 切镜像：`npm config set registry https://registry.npmmirror.com`，然后重新 `npm install`。如果之前已经装一半，先 `rm -rf node_modules package-lock.json`（Windows 上手动删）。

### Q5: 登录后侧边栏只看到一个空菜单

A: 用户的角色没有关联任何菜单。检查 `sys_role_menu` 表里该角色的关联记录。买家/卖家角色配置在 `sql/install/04_system_menus.sql` 末尾。

### Q6: 创作者认证审核通过时报 "未找到角色[creator]"

A: `04_system_menus.sql` 没执行或执行了旧版本。重新执行 §3.2 的 04 文件，确保 `sys_role` 表里有 `role_key='creator'` 一条记录。

### Q7: 注册的新用户在用户管理页看不到

A: 用户管理页左边部门树点 "买家" 部门即可。新注册用户默认进 buyer (200) 或 creator (201) 部门，不点部门或点错部门会被前端 deptId 参数过滤掉。

### Q8: 端口冲突

| 端口 | 用途 | 修改位置 |
|---|---|---|
| 8080 | 后端 | `ruoyi-admin/src/main/resources/application.yml` 的 `server.port` |
| 80 | 前端 dev | `ruoyi-ui/vue.config.js` 的 `port`，或 `npm run dev -- --port 8090` |
| 3306 | MySQL | `application-druid.yml` 主库 url |
| 6379 | Redis | `application.yml` 的 `spring.data.redis.port` |

---

## 8. 交付清单

接手项目时确认以下文件齐全：

```
<项目根目录>/
├── pom.xml                           # 父 pom
├── ruoyi-admin/                      # 启动模块
├── ruoyi-framework/                  # 框架核心
├── ruoyi-system/                     # 系统模块（含创作业务）
├── ruoyi-common/                     # 公共工具
├── ruoyi-quartz/                     # 定时任务
├── ruoyi-generator/                  # 代码生成器
├── ruoyi-ui/                         # 前端工程
├── sql/
│   ├── install/                      # 全量 SQL（5 个）
│   ├── *_upgrade_*.sql               # 增量补丁（旧库升级用，新装可忽略）
│   └── README.md                     # 数据库说明
└── docs/
    ├── setup.md                      # 本文（部署运行手册）
    ├── collaboration.md              # AI 协作开发记录
    └── project-review.md             # 项目复盘
```

---

## 9. 联系与反馈

- 项目协作记录：`docs/collaboration.md`
- 数据库说明：`sql/README.md`
- 开题对齐与需求矩阵：`docs/collaboration.md` 的"项目路线"章节
