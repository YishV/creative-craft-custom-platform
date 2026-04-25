package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.CreativeCategory;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeCategoryMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeProductService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeProductServiceImpl implements ICreativeProductService
{
    private static final String STATUS_ON_SHELF = "0";
    private static final String STATUS_OFF_SHELF = "1";

    @Autowired
    private CreativeProductMapper creativeProductMapper;

    @Autowired
    private CreativeCreatorMapper creativeCreatorMapper;

    @Autowired
    private CreativeCategoryMapper creativeCategoryMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeProduct selectCreativeProductByProductId(Long productId)
    {
        CreativeProduct product = requireProduct(productId);
        ensureCreatorOwned(product);
        return product;
    }

    @Override
    public List<CreativeProduct> selectCreativeProductList(CreativeProduct creativeProduct)
    {
        if (!permissionService.isAdmin())
        {
            Long creatorId = permissionService.getCurrentCreatorIdOrNull();
            if (creatorId == null)
            {
                return Collections.emptyList();
            }
            creativeProduct.setCreatorId(creatorId);
        }
        return creativeProductMapper.selectCreativeProductList(creativeProduct);
    }

    @Override
    public int insertCreativeProduct(CreativeProduct creativeProduct)
    {
        if (!permissionService.isAdmin())
        {
            creativeProduct.setCreatorId(permissionService.requireCurrentCreatorId());
        }
        return creativeProductMapper.insertCreativeProduct(creativeProduct);
    }

    @Override
    public int updateCreativeProduct(CreativeProduct creativeProduct)
    {
        CreativeProduct existing = requireProduct(creativeProduct.getProductId());
        ensureCreatorOwned(existing);
        creativeProduct.setCreatorId(existing.getCreatorId());
        return creativeProductMapper.updateCreativeProduct(creativeProduct);
    }

    @Override
    public int putOnShelf(Long productId, String operator)
    {
        CreativeProduct product = requireProduct(productId);
        ensureCreatorOwned(product);
        if (STATUS_ON_SHELF.equals(product.getStatus()))
        {
            throw new ServiceException("商品已处于上架状态");
        }
        validateProductForShelf(product);

        CreativeProduct update = new CreativeProduct();
        update.setProductId(productId);
        update.setStatus(STATUS_ON_SHELF);
        update.setUpdateBy(operator);
        return creativeProductMapper.updateCreativeProduct(update);
    }

    @Override
    public int takeOffShelf(Long productId, String operator)
    {
        CreativeProduct product = requireProduct(productId);
        ensureCreatorOwned(product);
        if (STATUS_OFF_SHELF.equals(product.getStatus()))
        {
            throw new ServiceException("商品已处于下架状态");
        }

        CreativeProduct update = new CreativeProduct();
        update.setProductId(productId);
        update.setStatus(STATUS_OFF_SHELF);
        update.setUpdateBy(operator);
        return creativeProductMapper.updateCreativeProduct(update);
    }

    @Override
    public int deleteCreativeProductByProductId(Long productId)
    {
        ensureCreatorOwned(requireProduct(productId));
        return creativeProductMapper.deleteCreativeProductByProductId(productId);
    }

    @Override
    public int deleteCreativeProductByProductIds(Long[] productIds)
    {
        if (productIds != null)
        {
            for (Long productId : productIds)
            {
                ensureCreatorOwned(requireProduct(productId));
            }
        }
        return creativeProductMapper.deleteCreativeProductByProductIds(productIds);
    }

    private CreativeProduct requireProduct(Long productId)
    {
        CreativeProduct product = creativeProductMapper.selectCreativeProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("商品不存在: " + productId);
        }
        return product;
    }

    private void ensureCreatorOwned(CreativeProduct product)
    {
        if (product == null || permissionService.isAdmin())
        {
            return;
        }
        Long creatorId = permissionService.requireCurrentCreatorId();
        if (!creatorId.equals(product.getCreatorId()))
        {
            throw new ServiceException("无权操作该数据");
        }
    }

    private void validateProductForShelf(CreativeProduct product)
    {
        if (StringUtils.isBlank(product.getProductName()))
        {
            throw new ServiceException("商品名称不能为空，不能上架");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("商品价格必须大于 0，不能上架");
        }
        if (product.getCreatorId() == null)
        {
            throw new ServiceException("商品未绑定创作者，不能上架");
        }
        if (product.getCategoryId() == null)
        {
            throw new ServiceException("商品未绑定分类，不能上架");
        }

        CreativeCreator creator = creativeCreatorMapper.selectCreativeCreatorByCreatorId(product.getCreatorId());
        if (creator == null)
        {
            throw new ServiceException("创作者不存在，不能上架");
        }
        if (!STATUS_ON_SHELF.equals(creator.getStatus()))
        {
            throw new ServiceException("创作者已停用，不能上架商品");
        }

        CreativeCategory category = creativeCategoryMapper.selectCreativeCategoryByCategoryId(product.getCategoryId());
        if (category == null)
        {
            throw new ServiceException("商品分类不存在，不能上架");
        }
        if (!STATUS_ON_SHELF.equals(category.getStatus()))
        {
            throw new ServiceException("商品分类已停用，不能上架商品");
        }
    }
}
