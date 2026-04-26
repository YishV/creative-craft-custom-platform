package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeQuote;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeQuoteMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeQuoteServiceImplTest
{
    @Mock
    private CreativeQuoteMapper creativeQuoteMapper;

    @Mock
    private CreativeDemandMapper creativeDemandMapper;

    @Mock
    private CreativeOrderMapper creativeOrderMapper;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeQuoteServiceImpl creativeQuoteService;

    @Test
    void insertCreativeQuoteShouldAssignCurrentCreatorIdForNonAdmin()
    {
        CreativeQuote quote = buildQuote(1L, 9L, 66L);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeDemandMapper.selectCreativeDemandByDemandId(9L)).thenReturn(buildDemand(7L, CreativeStatusFlow.Demand.PUBLISHED));
        when(creativeQuoteMapper.insertCreativeQuote(any(CreativeQuote.class))).thenReturn(1);

        int rows = creativeQuoteService.insertCreativeQuote(quote);

        assertEquals(1, rows);
        ArgumentCaptor<CreativeQuote> captor = ArgumentCaptor.forClass(CreativeQuote.class);
        verify(creativeQuoteMapper).insertCreativeQuote(captor.capture());
        assertEquals(11L, captor.getValue().getCreatorId());
        assertEquals(CreativeStatusFlow.Quote.PENDING, captor.getValue().getQuoteStatus());
    }

    @Test
    void updateCreativeQuoteShouldFailWhenQuoteNotOwnedByCurrentCreator()
    {
        when(creativeQuoteMapper.selectCreativeQuoteByQuoteId(1L)).thenReturn(buildQuote(1L, 9L, 66L));
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureCreatorOwned(eq(66L));

        CreativeQuote update = new CreativeQuote();
        update.setQuoteId(1L);
        update.setQuoteAmount(BigDecimal.valueOf(299));

        assertThrows(ServiceException.class, () -> creativeQuoteService.updateCreativeQuote(update));

        verify(creativeQuoteMapper, never()).updateCreativeQuote(any(CreativeQuote.class));
    }

    @Test
    void selectQuoteAndGenerateOrderShouldFailWhenDemandNotOwnedByCurrentBuyer()
    {
        when(creativeQuoteMapper.selectCreativeQuoteByQuoteId(1L)).thenReturn(buildQuote(1L, 9L, 11L));
        when(creativeDemandMapper.selectCreativeDemandByDemandId(9L))
            .thenReturn(buildDemand(5L, CreativeStatusFlow.Demand.QUOTING));
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureBuyerOwned(eq(5L));

        assertThrows(ServiceException.class, () -> creativeQuoteService.selectQuoteAndGenerateOrder(1L, "buyer"));

        verify(creativeOrderMapper, never()).insertCreativeOrder(any());
    }

    private CreativeQuote buildQuote(Long quoteId, Long demandId, Long creatorId)
    {
        CreativeQuote quote = new CreativeQuote();
        quote.setQuoteId(quoteId);
        quote.setDemandId(demandId);
        quote.setCreatorId(creatorId);
        quote.setQuoteAmount(BigDecimal.valueOf(199));
        quote.setDeliveryDays(7);
        quote.setQuoteStatus(CreativeStatusFlow.Quote.PENDING);
        quote.setCreateBy("creator");
        return quote;
    }

    private CreativeDemand buildDemand(Long userId, String status)
    {
        CreativeDemand demand = new CreativeDemand();
        demand.setDemandId(9L);
        demand.setUserId(userId);
        demand.setDemandStatus(status);
        demand.setDemandTitle("定制刺绣挂件");
        return demand;
    }
}
