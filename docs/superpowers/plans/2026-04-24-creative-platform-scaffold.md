# 文创手作定制交易平台基础骨架 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在若依前后端分离框架中搭建与毕业设计需求匹配的文创手作定制交易平台基础骨架。

**Architecture:** 后端在 `ruoyi-admin` 与 `ruoyi-system` 中新增 `creative` 业务层级，前端在 `ruoyi-ui` 中新增 `creative` 模块页面与 API 封装，并通过 SQL 脚本预留菜单与基础业务表。当前阶段只保证结构可扩展、命名统一、构建不报错。

**Tech Stack:** Spring Boot、Spring Security、MyBatis、Vue2、Element UI、MySQL

---

### Task 1: 固定模块边界与文档

**Files:**
- Create: `docs/superpowers/specs/2026-04-24-creative-platform-scaffold-design.md`
- Create: `docs/superpowers/plans/2026-04-24-creative-platform-scaffold.md`

- [ ] 写入设计说明，明确模块、命名、边界与延期项。
- [ ] 写入实施计划，固定后端、前端、SQL 三部分交付。

### Task 2: 建立后端领域骨架

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/creative/*.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/creative/*.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/*.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/*.java`
- Create: `ruoyi-system/src/main/resources/mapper/creative/*.xml`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/*.java`

- [ ] 为九个模块分别创建 Domain。
- [ ] 为九个模块分别创建 Mapper 接口与 XML。
- [ ] 为九个模块分别创建 Service 与 ServiceImpl。
- [ ] 为九个模块分别创建 Controller，并统一提供列表、详情、新增、修改、删除接口骨架。

### Task 3: 建立前端管理骨架

**Files:**
- Create: `ruoyi-ui/src/api/creative/*.js`
- Create: `ruoyi-ui/src/views/creative/*/index.vue`
- Modify: `ruoyi-ui/src/views/index.vue`

- [ ] 为各业务模块创建 API 封装。
- [ ] 为各业务模块创建后台管理占位页。
- [ ] 修改首页说明，使其贴合文创手作定制交易平台主题。

### Task 4: 预留数据库与菜单脚本

**Files:**
- Create: `sql/creative_platform_tables.sql`
- Create: `sql/creative_platform_menu.sql`

- [ ] 输出基础业务表草案。
- [ ] 输出菜单、按钮权限初始化脚本。

### Task 5: 验证骨架可编译

**Files:**
- Verify only

- [ ] 运行后端 Maven 编译验证新增 Java/XML 结构无语法错误。
- [ ] 运行前端构建验证新增页面与 API 引用无打包错误。
- [ ] 记录无法验证的外部依赖风险。
