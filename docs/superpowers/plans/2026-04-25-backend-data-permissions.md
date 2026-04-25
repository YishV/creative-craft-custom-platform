# 后端数据权限 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为文创模块补齐后端数据权限，让买家和创作者只能查询和操作自己的业务数据，同时保留管理员全量访问。

**Architecture:** 采用“Service 层统一归属校验，Controller 只补当前用户上下文”的方案。新增轻量业务权限辅助组件负责解析当前登录用户、管理员绕过和 `userId -> creatorId` 映射；各 Service 在列表查询、详情、修改、删除和状态流转中复用该组件完成数据收敛和所有权断言。

**Tech Stack:** Spring Boot, RuoYi SecurityUtils, MyBatis XML, JUnit 5, Mockito, Maven

---

### Task 1: 业务权限辅助组件

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/support/CreativeDataPermissionService.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/support/CreativeDataPermissionServiceTest.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeCreatorServiceImpl.java`

- [ ] **Step 1: 写失败测试，覆盖管理员绕过、有效创作者解析、无效创作者报错**

```java
@Test
void requireCurrentCreatorIdShouldReturnCreatorIdWhenCreatorIsApprovedAndNormal()
{
    when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
        .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

    Long creatorId = permissionService.requireCurrentCreatorId();

    assertEquals(88L, creatorId);
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDataPermissionServiceTest test`
Expected: FAIL，提示权限辅助组件或方法不存在

- [ ] **Step 3: 写最小实现**

```java
public Long requireCurrentCreatorId()
{
    if (SecurityUtils.isAdmin())
    {
        return null;
    }
    CreativeCreator query = new CreativeCreator();
    query.setUserId(SecurityUtils.getUserId());
    List<CreativeCreator> creators = creativeCreatorMapper.selectCreativeCreatorList(query);
    return creators.stream()
        .filter(this::isEffectiveCreator)
        .findFirst()
        .map(CreativeCreator::getCreatorId)
        .orElseThrow(() -> new ServiceException("当前用户未绑定有效创作者身份"));
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDataPermissionServiceTest test`
Expected: PASS

- [ ] **Step 5: 补管理员查询放行和无效创作者返回空的测试**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDataPermissionServiceTest test`

### Task 2: 买家侧 demand 与 favorite 权限

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeDemandServiceImpl.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeFavoriteServiceImpl.java`
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeDemandController.java`
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeFavoriteController.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeDemandServiceImplTest.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeFavoriteServiceImplTest.java`

- [ ] **Step 1: 写 demand 失败测试，覆盖列表自动按当前用户收敛、非本人更新失败**

```java
@Test
void selectCreativeDemandListShouldScopeToCurrentUser()
{
    CreativeDemand query = new CreativeDemand();

    creativeDemandService.selectCreativeDemandList(query);

    verify(permissionService).applyBuyerScope(query);
}
```

- [ ] **Step 2: 运行 demand 测试确认失败**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDemandServiceImplTest test`
Expected: FAIL，提示 service 尚未接入权限组件

- [ ] **Step 3: 实现 demand 最小逻辑**

```java
public List<CreativeDemand> selectCreativeDemandList(CreativeDemand creativeDemand)
{
    permissionService.applyBuyerScope(creativeDemand);
    return creativeDemandMapper.selectCreativeDemandList(creativeDemand);
}

public int updateCreativeDemand(CreativeDemand creativeDemand)
{
    CreativeDemand existing = requireOwnedDemand(creativeDemand.getDemandId());
    creativeDemand.setUserId(existing.getUserId());
    return creativeDemandMapper.updateCreativeDemand(creativeDemand);
}
```

- [ ] **Step 4: 写 favorite 失败测试，覆盖列表收敛、非本人删除失败**

```java
@Test
void deleteCreativeFavoriteByFavoriteIdShouldFailWhenFavoriteNotOwned()
{
    when(creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(1L)).thenReturn(buildFavorite(2L));

    assertThrows(ServiceException.class, () -> creativeFavoriteService.deleteCreativeFavoriteByFavoriteId(1L));
}
```

- [ ] **Step 5: 实现 favorite 最小逻辑并修改 controller 强制写入当前 `userId`**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDemandServiceImplTest,CreativeFavoriteServiceImplTest test`
Expected: PASS

### Task 3: 创作者侧 product 与 quote 权限

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeProductServiceImpl.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeQuoteServiceImpl.java`
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeProductController.java`
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeQuoteController.java`
- Modify: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeProductServiceImplTest.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeQuoteServiceImplTest.java`

- [ ] **Step 1: 扩写商品失败测试，覆盖他人商品上下架失败**

```java
@Test
void putOnShelfShouldFailWhenProductDoesNotBelongToCurrentCreator()
{
    when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "1", 66L));
    when(permissionService.requireCurrentCreatorId()).thenReturn(77L);

    assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));
}
```

- [ ] **Step 2: 运行商品测试确认失败**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeProductServiceImplTest test`
Expected: FAIL，提示缺少所有权校验

- [ ] **Step 3: 实现商品列表收敛、详情/修改/删除/上下架归属校验**

```java
private CreativeProduct requireOwnedProduct(Long productId)
{
    CreativeProduct product = creativeProductMapper.selectCreativeProductByProductId(productId);
    permissionService.assertCreatorOwned(product.getCreatorId());
    return product;
}
```

- [ ] **Step 4: 写 quote 失败测试，覆盖新增报价强制写当前 `creatorId`、非本人报价修改失败、买家不能选中他人需求报价**

```java
@Test
void selectQuoteAndGenerateOrderShouldFailWhenDemandNotOwnedByCurrentBuyer()
{
    when(creativeQuoteMapper.selectCreativeQuoteByQuoteId(1L)).thenReturn(buildQuote(1L, 9L));
    when(creativeDemandMapper.selectCreativeDemandByDemandId(9L)).thenReturn(buildDemand(5L));
    when(permissionService.getCurrentUserId()).thenReturn(8L);

    assertThrows(ServiceException.class, () -> creativeQuoteService.selectQuoteAndGenerateOrder(1L, "buyer"));
}
```

- [ ] **Step 5: 实现 quote 最小逻辑并修改 controller 强制写当前 `creatorId`**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeProductServiceImplTest,CreativeQuoteServiceImplTest test`
Expected: PASS

### Task 4: 订单权限

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/creative/impl/CreativeOrderServiceImpl.java`
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/creative/CreativeOrderController.java`
- Create: `ruoyi-system/src/test/java/com/ruoyi/system/service/creative/impl/CreativeOrderServiceImplTest.java`

- [ ] **Step 1: 写失败测试，覆盖买家列表按 `buyerId` 收敛、创作者列表按 `sellerId` 收敛**

```java
@Test
void selectCreativeOrderListShouldScopeBuyerOrdersForBuyerOnly()
{
    CreativeOrder query = new CreativeOrder();
    when(permissionService.getCurrentCreatorIdOrNull()).thenReturn(null);
    when(permissionService.getCurrentUserId()).thenReturn(3L);

    creativeOrderService.selectCreativeOrderList(query);

    assertEquals(3L, query.getBuyerId());
    assertNull(query.getSellerId());
}
```

- [ ] **Step 2: 运行订单测试确认失败**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeOrderServiceImplTest test`
Expected: FAIL，提示订单 service 尚未做角色侧过滤

- [ ] **Step 3: 实现订单列表、详情和状态推进所有权校验**

```java
private CreativeOrder requireAccessibleOrder(Long orderId)
{
    CreativeOrder order = creativeOrderMapper.selectCreativeOrderByOrderId(orderId);
    permissionService.assertOrderAccessible(order.getBuyerId(), order.getSellerId());
    return order;
}
```

- [ ] **Step 4: 运行订单测试确认通过**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeOrderServiceImplTest test`
Expected: PASS

- [ ] **Step 5: 补管理员绕过测试**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeOrderServiceImplTest test`

### Task 5: 收尾验证与文档回填

**Files:**
- Modify: `docs/collaboration.md`

- [ ] **Step 1: 运行文创权限相关 service 测试**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeDataPermissionServiceTest,CreativeDemandServiceImplTest,CreativeFavoriteServiceImplTest,CreativeProductServiceImplTest,CreativeQuoteServiceImplTest,CreativeOrderServiceImplTest test`
Expected: PASS

- [ ] **Step 2: 运行既有创作者审核测试，确保角色与权限逻辑未回归**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DfailIfNoTests=false -pl ruoyi-system -am -Dtest=CreativeCreatorServiceImplTest test`
Expected: PASS

- [ ] **Step 3: 运行后端打包**

Run: `mvn --% -Dmaven.repo.local=.m2repo -DskipTests -pl ruoyi-admin -am package`
Expected: BUILD SUCCESS

- [ ] **Step 4: 运行前端构建**

Run: `cd ruoyi-ui && npm.cmd run build:prod`
Expected: 构建成功，允许保留既有体积告警

- [ ] **Step 5: 更新协作文档，移除 `(@Codex WIP)` 并记录验证结果**

```text
记录任务：后端数据权限
完成人：Codex
验证：service tests + backend package + frontend build
```
