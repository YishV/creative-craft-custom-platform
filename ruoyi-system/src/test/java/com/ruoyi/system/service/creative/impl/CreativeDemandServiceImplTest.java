package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeDemandServiceImplTest
{
    @Mock
    private CreativeDemandMapper creativeDemandMapper;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeDemandServiceImpl creativeDemandService;

    @Test
    void updateCreativeDemandShouldFailWhenDemandNotOwnedByCurrentBuyer()
    {
        CreativeDemand existing = buildDemand(1L, 8L);
        when(creativeDemandMapper.selectCreativeDemandByDemandId(1L)).thenReturn(existing);
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureBuyerOwned(eq(8L));

        CreativeDemand update = new CreativeDemand();
        update.setDemandId(1L);
        update.setDemandTitle("修改标题");

        assertThrows(ServiceException.class, () -> creativeDemandService.updateCreativeDemand(update));

        verify(creativeDemandMapper, never()).updateCreativeDemand(any(CreativeDemand.class));
    }

    @Test
    void updateCreativeDemandShouldPreserveOriginalUserIdAfterOwnershipPasses()
    {
        CreativeDemand existing = buildDemand(1L, 8L);
        when(creativeDemandMapper.selectCreativeDemandByDemandId(1L)).thenReturn(existing);
        when(creativeDemandMapper.updateCreativeDemand(any(CreativeDemand.class))).thenReturn(1);

        CreativeDemand update = new CreativeDemand();
        update.setDemandId(1L);
        update.setUserId(999L);
        update.setDemandTitle("修改标题");

        creativeDemandService.updateCreativeDemand(update);

        verify(permissionService).ensureBuyerOwned(eq(8L));
        // userId 被强制回填为 existing.userId(8L)
        assert update.getUserId().equals(8L);
    }

    private CreativeDemand buildDemand(Long demandId, Long userId)
    {
        CreativeDemand demand = new CreativeDemand();
        demand.setDemandId(demandId);
        demand.setUserId(userId);
        demand.setCategoryId(2L);
        demand.setDemandTitle("定制陶艺摆件");
        demand.setBudgetAmount(BigDecimal.valueOf(200));
        demand.setDemandStatus("draft");
        return demand;
    }
}
