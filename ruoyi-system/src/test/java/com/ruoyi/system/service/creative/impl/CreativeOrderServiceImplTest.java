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
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeOrderServiceImplTest
{
    @Mock
    private CreativeOrderMapper creativeOrderMapper;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeOrderServiceImpl creativeOrderService;

    @Test
    void selectCreativeOrderListShouldMergeBuyerAndSellerOrders()
    {
        CreativeOrder buyerOrder = buildOrder(1L, 3L, 99L, CreativeStatusFlow.Order.CREATED);
        CreativeOrder sellerOrder = buildOrder(2L, 8L, 9L, CreativeStatusFlow.Order.MAKING);
        CreativeOrder sharedOrder = buildOrder(3L, 3L, 9L, CreativeStatusFlow.Order.SHIPPED);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(3L);
        when(permissionService.getCurrentCreatorIdOrNull()).thenReturn(9L);
        when(creativeOrderMapper.selectCreativeOrderList(any(CreativeOrder.class)))
            .thenReturn(List.of(buyerOrder, sharedOrder))
            .thenReturn(List.of(sellerOrder, sharedOrder));

        List<CreativeOrder> orders = creativeOrderService.selectCreativeOrderList(new CreativeOrder());

        assertEquals(3, orders.size());
        ArgumentCaptor<CreativeOrder> captor = ArgumentCaptor.forClass(CreativeOrder.class);
        verify(creativeOrderMapper, org.mockito.Mockito.times(2)).selectCreativeOrderList(captor.capture());
        assertEquals(3L, captor.getAllValues().get(0).getBuyerId());
        assertEquals(9L, captor.getAllValues().get(1).getSellerId());
    }

    @Test
    void selectCreativeOrderByOrderIdShouldFailWhenOrderNotAccessible()
    {
        CreativeOrder order = buildOrder(1L, 4L, 10L, CreativeStatusFlow.Order.CREATED);
        when(creativeOrderMapper.selectCreativeOrderByOrderId(1L)).thenReturn(order);
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureOrderOwned(eq(4L), eq(10L));

        assertThrows(ServiceException.class, () -> creativeOrderService.selectCreativeOrderByOrderId(1L));
    }

    @Test
    void transitOrderStatusShouldFailWhenOrderNotAccessible()
    {
        CreativeOrder order = buildOrder(1L, 4L, 10L, CreativeStatusFlow.Order.CREATED);
        when(creativeOrderMapper.selectCreativeOrderByOrderId(1L)).thenReturn(order);
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureOrderOwned(eq(4L), eq(10L));

        assertThrows(ServiceException.class,
            () -> creativeOrderService.transitOrderStatus(1L, CreativeStatusFlow.Order.MAKING, "codex"));

        verify(creativeOrderMapper, never()).updateCreativeOrder(any(CreativeOrder.class));
    }

    @Test
    void transitOrderStatusShouldRejectBuyerStartingProduction()
    {
        CreativeOrder order = buildOrder(1L, 4L, 10L, CreativeStatusFlow.Order.CREATED);
        when(creativeOrderMapper.selectCreativeOrderByOrderId(1L)).thenReturn(order);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentCreatorIdOrNull()).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class,
            () -> creativeOrderService.transitOrderStatus(1L, CreativeStatusFlow.Order.MAKING, "buyer"));

        assertEquals("只有创作者可以执行该订单操作", exception.getMessage());
        verify(creativeOrderMapper, never()).updateCreativeOrder(any(CreativeOrder.class));
    }

    @Test
    void transitOrderStatusShouldRejectCreatorFinishingOrder()
    {
        CreativeOrder order = buildOrder(1L, 4L, 10L, CreativeStatusFlow.Order.SHIPPED);
        when(creativeOrderMapper.selectCreativeOrderByOrderId(1L)).thenReturn(order);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(8L);

        ServiceException exception = assertThrows(ServiceException.class,
            () -> creativeOrderService.transitOrderStatus(1L, CreativeStatusFlow.Order.FINISHED, "creator"));

        assertEquals("只有买家可以执行该订单操作", exception.getMessage());
        verify(creativeOrderMapper, never()).updateCreativeOrder(any(CreativeOrder.class));
    }

    @Test
    void selectCreativeOrderByOrderIdShouldAllowOwner()
    {
        CreativeOrder order = buildOrder(1L, 4L, 10L, CreativeStatusFlow.Order.CREATED);
        when(creativeOrderMapper.selectCreativeOrderByOrderId(1L)).thenReturn(order);

        CreativeOrder result = creativeOrderService.selectCreativeOrderByOrderId(1L);

        assertEquals(1L, result.getOrderId());
        verify(permissionService).ensureOrderOwned(eq(4L), eq(10L));
    }

    private CreativeOrder buildOrder(Long orderId, Long buyerId, Long sellerId, String status)
    {
        CreativeOrder order = new CreativeOrder();
        order.setOrderId(orderId);
        order.setOrderNo("CRAFT" + orderId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setOrderAmount(BigDecimal.valueOf(199));
        order.setOrderStatus(status);
        return order;
    }
}
