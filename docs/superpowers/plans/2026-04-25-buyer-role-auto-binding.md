# 买家角色自动绑定 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为新注册用户自动绑定 `buyer` 角色，并补齐 `buyer` 初始化与历史用户回填脚本。

**Architecture:** 在 `ISysUserService` 中新增通用“按 roleKey 追加角色”能力，由 `registerUser` 和创作者审核共用。SQL 层新增 `buyer` 初始化与历史补绑脚本，并临时复制 `common` 权限以兼容当前无门户端现状。

**Tech Stack:** Spring Boot, MyBatis XML, RuoYi 权限模型, JUnit 5, Mockito, Maven

---

### Task 1: 角色追加能力测试

**Files:**
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/impl/SysUserServiceImplTest.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUserService.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUserServiceImpl.java`

- [ ] **Step 1: 写 `registerUser` 自动追加 `buyer` 的失败测试**
- [ ] **Step 2: 运行测试确认失败**
- [ ] **Step 3: 实现最小角色追加逻辑**
- [ ] **Step 4: 运行测试确认通过**
- [ ] **Step 5: 补“目标角色已存在时跳过”与“角色缺失时报错”测试**

### Task 2: 注册链路接入与创作者复用

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUserService.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUserServiceImpl.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeCreatorServiceImpl.java`
- Modify: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeCreatorServiceImplTest.java`

- [ ] **Step 1: 让创作者审核改用通用角色追加方法**
- [ ] **Step 2: 调整创作者审核测试使其验证新协作方式**
- [ ] **Step 3: 运行相关测试确认通过**

### Task 3: SQL 初始化与回填

**Files:**
- Create: `sql/buyer_role_upgrade_20260425.sql`
- Modify: `sql/ry_20260417.sql`
- Modify: `docs/collaboration.md`

- [ ] **Step 1: 新增 `buyer` 角色初始化 SQL**
- [ ] **Step 2: 补 `role_menu` / `role_dept` 复制与历史用户补绑 SQL**
- [ ] **Step 3: 在基础初始化脚本中补齐 `buyer` 角色定义**

### Task 4: 验证与收尾

**Files:**
- Modify: `docs/collaboration.md`

- [ ] **Step 1: 运行 `SysUserServiceImplTest`**
  Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=SysUserServiceImplTest test`
- [ ] **Step 2: 运行 `CreativeCreatorServiceImplTest`**
  Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeCreatorServiceImplTest test`
- [ ] **Step 3: 运行后端打包**
  Run: `mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`
- [ ] **Step 4: 运行前端构建**
  Run: `cd ruoyi-ui && npm.cmd run build:prod`
- [ ] **Step 5: 更新协作文档，移除 WIP 并记录验证结果**
