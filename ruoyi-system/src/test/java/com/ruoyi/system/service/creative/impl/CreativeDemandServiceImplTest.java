package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void selectCreativeDemandListShouldScopeToCurrentBuyer()
    {
        CreativeDemand query = new CreativeDemand();
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(7L);
        when(creativeDemandMapper.selectCreativeDemandList(any(CreativeDemand.class))).thenReturn(Collections.emptyList());

        creativeDemandService.selectCreativeDemandList(query);

        ArgumentCaptor<CreativeDemand> captor = ArgumentCaptor.forClass(CreativeDemand.class);
        verify(creativeDemandMapper).selectCreativeDemandList(captor.capture());
        assertEquals(7L, captor.getValue().getUserId());
    }

    @Test
    void updateCreativeDemandShouldFailWhenDemandNotOwnedByCurrentBuyer()
    {
        CreativeDemand existing = buildDemand(1L, 8L);
        when(creativeDemandMapper.selectCreativeDemandByDemandId(1L)).thenReturn(existing);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(7L);

        CreativeDemand update = new CreativeDemand();
        update.setDemandId(1L);
        update.setDemandTitle("修改标题");

        assertThrows(ServiceException.class, () -> creativeDemandService.updateCreativeDemand(update));

        verify(creativeDemandMapper, never()).updateCreativeDemand(any(CreativeDemand.class));
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
