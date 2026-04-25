package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeCategory;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeCategoryMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
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
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeProductServiceImpl creativeProductService;

    @Test
    void selectCreativeProductListShouldScopeToCurrentCreator()
    {
        CreativeProduct query = new CreativeProduct();
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentCreatorIdOrNull()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductList(any(CreativeProduct.class))).thenReturn(Collections.emptyList());

        creativeProductService.selectCreativeProductList(query);

        ArgumentCaptor<CreativeProduct> captor = ArgumentCaptor.forClass(CreativeProduct.class);
        verify(creativeProductMapper).selectCreativeProductList(captor.capture());
        assertEquals(11L, captor.getValue().getCreatorId());
    }

    @Test
    void putOnShelfShouldUpdateStatusWhenProductIsValid()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("0"));
        when(creativeCategoryMapper.selectCreativeCategoryByCategoryId(21L)).thenReturn(buildCategory("0"));
        when(creativeProductMapper.updateCreativeProduct(any(CreativeProduct.class))).thenReturn(1);

        int rows = creativeProductService.putOnShelf(1L, "codex");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeProduct> captor = ArgumentCaptor.forClass(CreativeProduct.class);
        verify(creativeProductMapper).updateCreativeProduct(captor.capture());
        assertEquals(1L, captor.getValue().getProductId());
        assertEquals("0", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getUpdateBy());
    }

    @Test
    void putOnShelfShouldFailWhenProductAlreadyOnShelf()
    {
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "0", 11L));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenCreatorIsDisabled()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("1"));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenCategoryIsDisabled()
    {
        CreativeProduct product = buildProduct(1L, "1", 11L);
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(product);
        when(creativeCreatorMapper.selectCreativeCreatorByCreatorId(11L)).thenReturn(buildCreator("0"));
        when(creativeCategoryMapper.selectCreativeCategoryByCategoryId(21L)).thenReturn(buildCategory("1"));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void putOnShelfShouldFailWhenProductDoesNotBelongToCurrentCreator()
    {
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(77L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "1", 66L));

        assertThrows(ServiceException.class, () -> creativeProductService.putOnShelf(1L, "codex"));

        verify(creativeProductMapper, never()).updateCreativeProduct(any(CreativeProduct.class));
    }

    @Test
    void takeOffShelfShouldUpdateStatusWhenProductIsOnShelf()
    {
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "0", 11L));
        when(creativeProductMapper.updateCreativeProduct(any(CreativeProduct.class))).thenReturn(1);

        int rows = creativeProductService.takeOffShelf(1L, "codex");

        assertEquals(1, rows);
        ArgumentCaptor<CreativeProduct> captor = ArgumentCaptor.forClass(CreativeProduct.class);
        verify(creativeProductMapper).updateCreativeProduct(captor.capture());
        assertEquals(1L, captor.getValue().getProductId());
        assertEquals("1", captor.getValue().getStatus());
        assertEquals("codex", captor.getValue().getUpdateBy());
    }

    @Test
    void takeOffShelfShouldFailWhenProductAlreadyOffShelf()
    {
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.requireCurrentCreatorId()).thenReturn(11L);
        when(creativeProductMapper.selectCreativeProductByProductId(1L)).thenReturn(buildProduct(1L, "1", 11L));

        assertThrows(ServiceException.class, () -> creativeProductService.takeOffShelf(1L, "codex"));

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
        return product;
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
