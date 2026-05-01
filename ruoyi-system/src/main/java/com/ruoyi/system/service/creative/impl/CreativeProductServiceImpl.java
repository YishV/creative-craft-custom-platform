package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.annotation.CreativeDataScope;
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
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeProductServiceImpl implements ICreativeProductService
{
    private static final String STATUS_ON_SHELF = "0";
    private static final String STATUS_OFF_SHELF = "1";
    private static final String AUDIT_PENDING = "pending";
    private static final String AUDIT_APPROVED = "approved";
    private static final String AUDIT_REJECTED = "rejected";

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
        permissionService.ensureCreatorOwned(product.getCreatorId());
        return product;
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.CREATOR, field = "creatorId")
    public List<CreativeProduct> selectCreativeProductList(CreativeProduct creativeProduct)
    {
        return creativeProductMapper.selectCreativeProductList(creativeProduct);
    }

    @Override
    public int insertCreativeProduct(CreativeProduct creativeProduct)
    {
        if (!permissionService.isAdmin())
        {
            creativeProduct.setCreatorId(permissionService.requireCurrentCreatorId());
        }
        // 新建商品默认下架 + 待审核（管理员显式传 approved 时除外）
        if (StringUtils.isBlank(creativeProduct.getStatus()))
        {
            creativeProduct.setStatus(STATUS_OFF_SHELF);
        }
        if (StringUtils.isBlank(creativeProduct.getAuditStatus()))
        {
            creativeProduct.setAuditStatus(AUDIT_PENDING);
        }
        return creativeProductMapper.insertCreativeProduct(creativeProduct);
    }

    @Override
    public int updateCreativeProduct(CreativeProduct creativeProduct)
    {
        CreativeProduct existing = requireProduct(creativeProduct.getProductId());
        permissionService.ensureCreatorOwned(existing.getCreatorId());
        creativeProduct.setCreatorId(existing.getCreatorId());
        return creativeProductMapper.updateCreativeProduct(creativeProduct);
    }

    @Override
    public int putOnShelf(Long productId, String operator)
    {
        CreativeProduct product = requireProduct(productId);
        permissionService.ensureCreatorOwned(product.getCreatorId());
        if (STATUS_ON_SHELF.equals(product.getStatus()))
        {
            throw new ServiceException("商品已处于上架状态");
        }
        if (!AUDIT_APPROVED.equals(product.getAuditStatus()))
        {
            throw new ServiceException("商品未通过审核，不能上架");
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
        permissionService.ensureCreatorOwned(product.getCreatorId());
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
    public int submitAudit(Long productId, String operator)
    {
        CreativeProduct product = requireProduct(productId);
        permissionService.ensureCreatorOwned(product.getCreatorId());
        String current = product.getAuditStatus();
        if (AUDIT_APPROVED.equals(current))
        {
            throw new ServiceException("商品已审核通过，无需重复提交");
        }
        if (AUDIT_PENDING.equals(current))
        {
            throw new ServiceException("商品已在审核中");
        }
        CreativeProduct update = new CreativeProduct();
        update.setProductId(productId);
        update.setAuditStatus(AUDIT_PENDING);
        update.setAuditRemark("");
        update.setUpdateBy(operator);
        return creativeProductMapper.updateCreativeProduct(update);
    }

    @Override
    public int approveAudit(Long productId, String remark, String operator)
    {
        if (!permissionService.isAdmin())
        {
            throw new ServiceException("仅管理员可以审核商品");
        }
        CreativeProduct product = requireProduct(productId);
        if (AUDIT_APPROVED.equals(product.getAuditStatus()))
        {
            throw new ServiceException("商品已审核通过");
        }
        CreativeProduct update = new CreativeProduct();
        update.setProductId(productId);
        update.setAuditStatus(AUDIT_APPROVED);
        update.setAuditRemark(remark);
        update.setAuditBy(operator);
        update.setAuditTime(new Date());
        update.setUpdateBy(operator);
        return creativeProductMapper.updateCreativeProduct(update);
    }

    @Override
    public int rejectAudit(Long productId, String remark, String operator)
    {
        if (!permissionService.isAdmin())
        {
            throw new ServiceException("仅管理员可以审核商品");
        }
        if (StringUtils.isBlank(remark))
        {
            throw new ServiceException("驳回必须填写理由，方便创作者修改");
        }
        CreativeProduct product = requireProduct(productId);
        if (AUDIT_REJECTED.equals(product.getAuditStatus()))
        {
            throw new ServiceException("商品已被驳回");
        }
        CreativeProduct update = new CreativeProduct();
        update.setProductId(productId);
        update.setAuditStatus(AUDIT_REJECTED);
        update.setAuditRemark(remark);
        update.setAuditBy(operator);
        update.setAuditTime(new Date());
        // 驳回同时下架
        update.setStatus(STATUS_OFF_SHELF);
        update.setUpdateBy(operator);
        return creativeProductMapper.updateCreativeProduct(update);
    }

    @Override
    public int deleteCreativeProductByProductId(Long productId)
    {
        permissionService.ensureCreatorOwned(requireProduct(productId).getCreatorId());
        return creativeProductMapper.deleteCreativeProductByProductId(productId);
    }

    @Override
    public int deleteCreativeProductByProductIds(Long[] productIds)
    {
        if (productIds != null)
        {
            for (Long productId : productIds)
            {
                permissionService.ensureCreatorOwned(requireProduct(productId).getCreatorId());
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
