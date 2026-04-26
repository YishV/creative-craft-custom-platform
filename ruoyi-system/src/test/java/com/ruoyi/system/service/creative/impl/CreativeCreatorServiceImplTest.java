package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeCreatorProfile;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeQuote;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.mapper.creative.CreativeQuoteMapper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeCreatorServiceImplTest
{
    @Mock
    private CreativeCreatorMapper creativeCreatorMapper;

    @Mock
    private CreativeProductMapper creativeProductMapper;

    @Mock
    private CreativeQuoteMapper creativeQuoteMapper;

    @Mock
    private CreativeOrderMapper creativeOrderMapper;

    @Mock
    private ISysUserService sysUserService;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeCreatorServiceImpl creativeCreatorService;

    @Test
    void applyCreatorShouldCreatePendingApplication()
    {
        CreativeCreator creator = buildCreator();
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class))).thenReturn(Collections.emptyList());
        when(creativeCreatorMapper.insertCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.applyCreator(creator);

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> captor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).insertCreativeCreator(captor.capture());
        assertEquals("pending", captor.getValue().getAuditStatus());
        assertEquals("1", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getCreateBy());
    }

    @Test
    void applyCreatorShouldFailWhenPendingOrApprovedApplicationExists()
    {
        CreativeCreator creator = buildCreator();
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(existingCreator("pending")));

        assertThrows(ServiceException.class, () -> creativeCreatorService.applyCreator(creator));

        verify(creativeCreatorMapper, never()).insertCreativeCreator(any(CreativeCreator.class));
    }

    @Test
    void approveCreatorShouldAppendCreatorRole()
    {
        CreativeCreator creator = existingCreator("pending");
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(creator);
        when(sysUserService.selectUserById(100L)).thenReturn(buildUser(100L));
        when(creativeCreatorMapper.updateCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.approveCreator(1L, "admin");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> creatorCaptor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).updateCreativeCreator(creatorCaptor.capture());
        assertEquals("approved", creatorCaptor.getValue().getAuditStatus());
        assertEquals("0", creatorCaptor.getValue().getStatus());
        assertEquals("admin", creatorCaptor.getValue().getAuditBy());
        verify(sysUserService).appendRoleByKeyIfAbsent(100L, "creator");
    }

    @Test
    void approveCreatorShouldFailWhenApplicationAlreadyReviewed()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("approved"));

        assertThrows(ServiceException.class, () -> creativeCreatorService.approveCreator(1L, "admin"));

        verify(creativeCreatorMapper, never()).updateCreativeCreator(any(CreativeCreator.class));
        verify(sysUserService, never()).appendRoleByKeyIfAbsent(any(), any());
    }

    @Test
    void rejectCreatorShouldStoreRejectReason()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("pending"));
        when(creativeCreatorMapper.updateCreativeCreator(any(CreativeCreator.class))).thenReturn(1);

        int rows = creativeCreatorService.rejectCreator(1L, "资料不完整", "admin");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeCreator> creatorCaptor = ArgumentCaptor.forClass(CreativeCreator.class);
        verify(creativeCreatorMapper).updateCreativeCreator(creatorCaptor.capture());
        assertEquals("rejected", creatorCaptor.getValue().getAuditStatus());
        assertEquals("资料不完整", creatorCaptor.getValue().getAuditRemark());
        assertEquals("1", creatorCaptor.getValue().getStatus());
    }

    @Test
    void rejectCreatorShouldFailWhenReasonIsBlank()
    {
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(1L)).thenReturn(existingCreator("pending"));

        assertThrows(ServiceException.class, () -> creativeCreatorService.rejectCreator(1L, "  ", "admin"));

        verify(creativeCreatorMapper, never()).updateCreativeCreator(any(CreativeCreator.class));
    }

    @Test
    void selectMyCreatorProfileShouldReturnEmptyWhenUserHasNoApplication()
    {
        when(permissionService.getCurrentUserId()).thenReturn(100L);
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class))).thenReturn(Collections.emptyList());

        CreativeCreatorProfile profile = creativeCreatorService.selectMyCreatorProfile();

        org.junit.jupiter.api.Assertions.assertNull(profile.getCreator());
        assertEquals(0L, profile.getProductCount());
        assertEquals(BigDecimal.ZERO, profile.getCompletedRevenue());
    }

    @Test
    void selectMyCreatorProfileShouldReturnPendingProfileWithoutStatistics()
    {
        when(permissionService.getCurrentUserId()).thenReturn(100L);
        CreativeCreator pending = existingCreator("pending");
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class))).thenReturn(List.of(pending));

        CreativeCreatorProfile profile = creativeCreatorService.selectMyCreatorProfile();

        assertEquals("pending", profile.getCreator().getAuditStatus());
        assertEquals(0L, profile.getProductCount());
        assertEquals(0L, profile.getActiveOrderCount());
        verify(creativeProductMapper, never()).selectCreativeProductList(any(CreativeProduct.class));
    }

    @Test
    void selectMyCreatorProfileShouldFillStatisticsForApprovedCreator()
    {
        when(permissionService.getCurrentUserId()).thenReturn(100L);
        CreativeCreator approved = existingCreator("approved");
        approved.setStatus("0");
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class))).thenReturn(List.of(approved));

        CreativeProduct onShelf = new CreativeProduct();
        onShelf.setStatus("0");
        CreativeProduct offShelf = new CreativeProduct();
        offShelf.setStatus("1");
        when(creativeProductMapper.selectCreativeProductList(any(CreativeProduct.class)))
            .thenReturn(List.of(onShelf, onShelf, offShelf));

        when(creativeQuoteMapper.selectCreativeQuoteList(any(CreativeQuote.class)))
            .thenReturn(List.of(new CreativeQuote(), new CreativeQuote()));

        CreativeOrder created = orderWithStatus(CreativeStatusFlow.Order.CREATED, BigDecimal.valueOf(100));
        CreativeOrder making = orderWithStatus(CreativeStatusFlow.Order.MAKING, BigDecimal.valueOf(200));
        CreativeOrder finished1 = orderWithStatus(CreativeStatusFlow.Order.FINISHED, BigDecimal.valueOf(300));
        CreativeOrder finished2 = orderWithStatus(CreativeStatusFlow.Order.FINISHED, BigDecimal.valueOf(150));
        CreativeOrder cancelled = orderWithStatus(CreativeStatusFlow.Order.CANCELLED, BigDecimal.valueOf(50));
        when(creativeOrderMapper.selectCreativeOrderList(any(CreativeOrder.class)))
            .thenReturn(List.of(created, making, finished1, finished2, cancelled));

        CreativeCreatorProfile profile = creativeCreatorService.selectMyCreatorProfile();

        assertEquals(3L, profile.getProductCount());
        assertEquals(2L, profile.getOnShelfProductCount());
        assertEquals(2L, profile.getPendingQuoteCount());
        assertEquals(2L, profile.getActiveOrderCount());
        assertEquals(2L, profile.getCompletedOrderCount());
        assertEquals(BigDecimal.valueOf(450), profile.getCompletedRevenue());
    }

    private CreativeOrder orderWithStatus(String status, BigDecimal amount)
    {
        CreativeOrder order = new CreativeOrder();
        order.setOrderStatus(status);
        order.setOrderAmount(amount);
        return order;
    }

    private CreativeCreator buildCreator()
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setUserId(100L);
        creator.setCreatorName("手作爱好者");
        creator.setStoreName("纸间工坊");
        creator.setCreatorLevel("newbie");
        creator.setCreateBy("codex");
        return creator;
    }

    private CreativeCreator existingCreator(String auditStatus)
    {
        CreativeCreator creator = buildCreator();
        creator.setCreatorId(1L);
        creator.setStatus("1");
        creator.setAuditStatus(auditStatus);
        return creator;
    }

    private SysUser buildUser(Long userId)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName("user" + userId);
        return user;
    }
}
