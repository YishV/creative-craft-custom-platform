package com.ruoyi.system.service.creative.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeDataPermissionServiceTest
{
    @Mock
    private CreativeCreatorMapper creativeCreatorMapper;

    @Spy
    @InjectMocks
    private CreativeDataPermissionService permissionService;

    @Test
    void requireCurrentCreatorIdShouldReturnCreatorIdWhenCreatorIsApprovedAndNormal()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

        Long creatorId = permissionService.requireCurrentCreatorId();

        assertEquals(88L, creatorId);
    }

    @Test
    void getCurrentCreatorIdOrNullShouldReturnNullWhenNoEffectiveCreatorExists()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(Collections.singletonList(buildCreator(88L, 7L, "1", "approved")));

        Long creatorId = permissionService.getCurrentCreatorIdOrNull();

        assertNull(creatorId);
    }

    @Test
    void requireCurrentCreatorIdShouldFailWhenNoEffectiveCreatorExists()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(Collections.emptyList());

        assertThrows(ServiceException.class, () -> permissionService.requireCurrentCreatorId());
    }

    @Test
    void applyListScopeShouldInjectBuyerIdForNonAdmin()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        CreativeDemand query = new CreativeDemand();

        boolean proceed = permissionService.applyListScope(query, CreativeDataScope.Owner.BUYER, "userId");

        assertTrue(proceed);
        assertEquals(7L, query.getUserId());
    }

    @Test
    void applyListScopeShouldKeepQueryUntouchedForAdmin()
    {
        doReturn(true).when(permissionService).isAdmin();
        CreativeDemand query = new CreativeDemand();
        query.setUserId(99L);

        boolean proceed = permissionService.applyListScope(query, CreativeDataScope.Owner.BUYER, "userId");

        assertTrue(proceed);
        assertEquals(99L, query.getUserId());
    }

    @Test
    void applyListScopeShouldInjectCreatorIdWhenCreatorPresent()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));
        CreativeProduct query = new CreativeProduct();

        boolean proceed = permissionService.applyListScope(query, CreativeDataScope.Owner.CREATOR, "creatorId");

        assertTrue(proceed);
        assertEquals(88L, query.getCreatorId());
    }

    @Test
    void applyListScopeShouldShortCircuitWhenCreatorAbsent()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(Collections.emptyList());
        CreativeProduct query = new CreativeProduct();

        boolean proceed = permissionService.applyListScope(query, CreativeDataScope.Owner.CREATOR, "creatorId");

        assertFalse(proceed);
        assertNull(query.getCreatorId());
    }

    @Test
    void ensureBuyerOwnedShouldThrowWhenMismatch()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();

        assertThrows(ServiceException.class, () -> permissionService.ensureBuyerOwned(8L));
    }

    @Test
    void ensureBuyerOwnedShouldPassForAdmin()
    {
        doReturn(true).when(permissionService).isAdmin();

        permissionService.ensureBuyerOwned(8L);
    }

    @Test
    void ensureCreatorOwnedShouldThrowWhenMismatch()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

        assertThrows(ServiceException.class, () -> permissionService.ensureCreatorOwned(99L));
    }

    @Test
    void ensureOrderOwnedShouldAllowBuyer()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();

        permissionService.ensureOrderOwned(7L, 999L);
    }

    @Test
    void ensureOrderOwnedShouldAllowSeller()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

        permissionService.ensureOrderOwned(999L, 88L);
    }

    @Test
    void ensureOrderOwnedShouldThrowWhenNeitherBuyerNorSeller()
    {
        doReturn(false).when(permissionService).isAdmin();
        doReturn(7L).when(permissionService).getCurrentUserId();
        when(creativeCreatorMapper.selectCreativeCreatorList(any(CreativeCreator.class)))
            .thenReturn(List.of(buildCreator(88L, 7L, "0", "approved")));

        assertThrows(ServiceException.class, () -> permissionService.ensureOrderOwned(999L, 999L));
    }

    private CreativeCreator buildCreator(Long creatorId, Long userId, String status, String auditStatus)
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setCreatorId(creatorId);
        creator.setUserId(userId);
        creator.setStatus(status);
        creator.setAuditStatus(auditStatus);
        return creator;
    }
}
