# 创作者认证审核流程实现计划

日期：`2026-04-25`

## 目标

完成阶段 2 的创作者认证审核闭环：

- 普通用户提交创作者申请
- 管理员审核通过或驳回
- 审核通过时追加 `creator` 角色
- 后台创作者管理页展示审核状态并提供审核动作

## 范围

- 补 `creative_creator` 审核字段与 SQL 升级脚本
- 实现 `apply / approve / reject` 后端接口
- 维持现有 CRUD，审核动作不混入通用编辑
- 改造后台创作者管理页接入审核状态和审核按钮
- 增加 service 层单测，覆盖申请、通过、驳回和重复校验

## 实施步骤

1. 文档认领
- 在 `docs/collaboration.md` 标记 `(@Codex WIP)`
- 记录本次设计结论，避免后续实现偏离

2. 测试先行
- 新增 `CreativeCreatorServiceImplTest`
- 覆盖以下场景：
  - `apply` 成功创建待审核申请
  - 重复申请失败
  - `approve` 成功并追加创作者角色
  - 非 `pending` 记录重复通过失败
  - `reject` 成功写入驳回原因
  - 空驳回原因失败

3. 后端实现
- `CreativeCreator` 增加审核字段
- `CreativeCreatorMapper.xml` 扩展字段映射、查询条件、插入更新 SQL
- `ICreativeCreatorService` / `CreativeCreatorServiceImpl` 增加：
  - `applyCreator`
  - `approveCreator`
  - `rejectCreator`
- 复用 `ISysUserService` 做用户存在性校验
- 复用 `SysRoleMapper` / `SysUserRoleMapper` 追加 `creator` 角色，不覆盖已有角色

4. 控制器与前端
- `CreativeCreatorController` 新增：
  - `POST /creative/creator/apply`
  - `POST /creative/creator/{creatorId}/approve`
  - `POST /creative/creator/{creatorId}/reject`
- `ruoyi-ui/src/api/creative/creator.js` 增加对应 API
- `ruoyi-ui/src/views/creative/creator/index.vue`：
  - 展示审核状态、审核备注
  - 待审核行显示通过/驳回按钮
  - 新增表单走 `apply`
  - 编辑表单不直接维护审核状态

5. SQL 交付
- 更新 `sql/creative_platform_tables.sql` 的 `creative_creator` 表结构
- 新增增量脚本，为现有库补字段并插入 `creator` 角色

6. 验证
- `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeCreatorServiceImplTest test`
- `mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`
- `cd ruoyi-ui && npm.cmd run build:prod`

## 风险与取舍

- 当前不做普通用户独立申请页，只交付接口和后台接入
- 当前不新增独立申请表，继续复用 `creative_creator`
- 当前不细分新权限点，审核动作复用 `creative:creator:edit`
