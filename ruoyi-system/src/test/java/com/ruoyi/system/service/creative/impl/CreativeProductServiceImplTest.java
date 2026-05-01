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
import com.ruoyi.system.domain.creative.CreativeCategory;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeCategoryMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeProductServiceImplTest
{
    @Mock
    private CreativeProductMapper creativeProductMapper;

    @Mock
    private CreativeCreatorMapper creativeCreatorMapper;

    @Mock
    private CreativeCategoryMapper creativeCategoryMapper;

    @Mock
    private CreativeOrderMapper creativeOrderMapper;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeProductServiceImpl creativeProductService;

    @Test
    void putOnShelfShouldUpdateStatusWhenProductIsValid()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("0"));
        when(creativeCategoryMapper.selectCreativeCategoryByCategoryId(21L)).thenReturn(buildCategory("0"));
        when(creativeProductMapper.updateCreativeProduct(any(CreativeProduct.class))).thenReturn(1);

        int rows = creativeProductService.putOnShelf(1L, "codex");

        assertEquals(1, rows);
        verify(permissionService).ensureCreatorOwned(eq(11L));
        ArgumentCaptor<CreativeProduct> captor = ArgumentCaptor.forClass(CreativeProduct.class);
        verify(creativeProductMapper).updateCreativeProduct(captor.capture());
        assertEquals(1L, captor.getValue().getProductId());
        assertEquals("0", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getUpdateBy());
    }

    @Test
    void putOnShelfShouldFailWhenProductAlreadyOnShelf()
    {
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "0", 11L));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenCreatorIsDisabled()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("1"));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenCategoryIsDisabled()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("0"));
        when(creativeCategoryMapper.selectCreativeCategoryByCategoryId(21L)).thenReturn(buildCategory("1"));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenProductDoesNotBelongToCurrentCreator()
    {
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "1", 66L));
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureCreatorOwned(eq(66L));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void takeOffShelfShouldUpdateStatusWhenProductIsOnShelf()
    {
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "0", 11L));
        when(creativeOrderMapper.selectCreativeOrderList(any(CreativeOrder.class))).thenReturn(Collections.emptyList());
        when(creativeProductMapper.updateCreativeProduct(any(CreativeProduct.class))).thenReturn(1);

        int rows = creativeProductService.takeOffShelf(1L, "codex");

        assertEquals(1, rows);
        verify(permissionService).ensureCreatorOwned(eq(11L));
        ArgumentCaptor<CreativeProduct> captor = ArgumentCaptor.forClass(CreativeProduct.class);
        verify(creativeProductMapper).updateCreativeProduct(captor.capture());
        assertEquals(1L, captor.getValue().getProductId());
        assertEquals("1", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getUpdateBy());
    }

    @Test
    void takeOffShelfShouldFailWhenProductAlreadyOffShelf()
    {
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "1", 11L));

        assertThrows(ServiceException.class, () -> creativeProductService.takeOffShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void takeOffShelfShouldFailWhenProductHasUnfinishedOrder()
    {
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "0", 11L));
        when(creativeOrderMapper.selectCreativeOrderList(any(CreativeOrder.class)))
            .thenReturn(Collections.singletonList(buildOrder(CreativeStatusFlow.Order.MAKING)));

        ServiceException exception = assertThrows(ServiceException.class,
            () -> creativeProductService.takeOffShelf(1L, "codex"));

        assertEquals("商品存在未完成订单，不能下架", exception.getMessage());
        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    private CreativeProduct buildProduct(Long productId, String status, Long creatorId)
    {
        CreativeProduct product = new CreativeProduct();
        product.setProductId(productId);
        product.setCreatorId(creatorId);
        product.setCategoryId(21L);
        product.setProductName("刺绣手包");
        product.setProductType("spot");
        product.setPrice(java.math.BigDecimal.valueOf(199.00));
        product.setStatus(status);
        product.setAuditStatus("approved");
        return product;
    }

    private CreativeOrder buildOrder(String status)
    {
        CreativeOrder order = new CreativeOrder();
        order.setOrderId(99L);
        order.setOrderStatus(status);
        order.setSourceType("product");
        order.setSourceId(1L);
        return order;
    }

    private CreativeCreator buildCreator(String status)
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setCreatorId(11L);
        creator.setCreatorName("创作者A");
        creator.setStatus(status);
        return creator;
    }

    private CreativeCategory buildCategory(String status)
    {
        CreativeCategory category = new CreativeCategory();
        category.setCategoryId(21L);
        category.setCategoryName("刺绣");
        category.setStatus(status);
        return category;
    }
}
