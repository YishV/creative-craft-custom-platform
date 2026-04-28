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
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeProductOrderRequest;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
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
    private CreativeProductMapper creativeProductMapper;

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

    @Test
    void createProductOrderShouldInsertOrderFromOnShelfProduct()
    {
        CreativeProduct product = buildProduct(12L, 6L, "手作杯垫", BigDecimal.valueOf(39.90), "0");
        when(creativeProductMapper.selectCreativeProductByProductId(12L)).thenReturn(product);
        when(permissionService.getCurrentUserId()).thenReturn(5L);

        CreativeProductOrderRequest request = new CreativeProductOrderRequest();
        request.setProductId(12L);
        request.setQuantity(2);
        request.setReceiverName("易水寒");
        request.setReceiverPhone("13800000000");
        request.setReceiverAddress("浙江省杭州市");

        CreativeOrder result = creativeOrderService.createProductOrder(request, "buyer");

        ArgumentCaptor<CreativeOrder> captor = ArgumentCaptor.forClass(CreativeOrder.class);
        verify(creativeOrderMapper).insertCreativeOrder(captor.capture());
        CreativeOrder inserted = captor.getValue();
        assertEquals(5L, inserted.getBuyerId());
        assertEquals(6L, inserted.getSellerId());
        assertEquals(0, BigDecimal.valueOf(79.80).compareTo(inserted.getOrderAmount()));
        assertEquals("created", inserted.getOrderStatus());
        assertEquals("unpaid", inserted.getPayStatus());
        assertEquals("product", inserted.getSourceType());
        assertEquals(12L, inserted.getSourceId());
        assertEquals("手作杯垫", inserted.getSourceName());
        assertEquals(2, inserted.getQuantity());
        assertEquals("易水寒 / 13800000000 / 浙江省杭州市", inserted.getAddressSnapshot());
        assertEquals(inserted, result);
    }

    @Test
    void createProductOrderShouldRejectOffShelfProduct()
    {
        CreativeProduct product = buildProduct(12L, 6L, "手作杯垫", BigDecimal.valueOf(39.90), "1");
        when(creativeProductMapper.selectCreativeProductByProductId(12L)).thenReturn(product);

        CreativeProductOrderRequest request = new CreativeProductOrderRequest();
        request.setProductId(12L);
        request.setQuantity(1);
        request.setReceiverName("易水寒");
        request.setReceiverPhone("13800000000");
        request.setReceiverAddress("浙江省杭州市");

        ServiceException exception = assertThrows(ServiceException.class,
            () -> creativeOrderService.createProductOrder(request, "buyer"));

        assertEquals("商品不存在或已下架", exception.getMessage());
        verify(creativeOrderMapper, never()).insertCreativeOrder(any(CreativeOrder.class));
    }

    @Test
    void payOrderShouldMarkBuyerOrderPaid()
    {
        CreativeOrder order = buildOrder(7L, 5L, 6L, CreativeStatusFlow.Order.CREATED);
        order.setPayStatus("unpaid");
        when(creativeOrderMapper.selectCreativeOrderByOrderId(7L)).thenReturn(order);

        creativeOrderService.payOrder(7L, "buyer");

        ArgumentCaptor<CreativeOrder> captor = ArgumentCaptor.forClass(CreativeOrder.class);
        verify(creativeOrderMapper).updateCreativeOrder(captor.capture());
        assertEquals("paid", captor.getValue().getPayStatus());
        verify(permissionService).ensureBuyerOwned(5L);
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

    private CreativeProduct buildProduct(Long productId, Long creatorId, String name, BigDecimal price, String status)
    {
        CreativeProduct product = new CreativeProduct();
        product.setProductId(productId);
        product.setCreatorId(creatorId);
        product.setProductName(name);
        product.setPrice(price);
        product.setStatus(status);
        return product;
    }
}
