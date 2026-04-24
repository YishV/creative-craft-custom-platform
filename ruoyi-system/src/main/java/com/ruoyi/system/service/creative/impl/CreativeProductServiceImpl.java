package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeProductService;

@Service
public class CreativeProductServiceImpl implements ICreativeProductService
{
    @Autowired
    private CreativeProductMapper creativeProductMapper;

    @Override
    public CreativeProduct selectCreativeProductByProductId(Long productId)
    {
        return creativeProductMapper.selectCreativeProductByProductId(productId);
    }

    @Override
    public List<CreativeProduct> selectCreativeProductList(CreativeProduct creativeProduct)
    {
        return creativeProductMapper.selectCreativeProductList(creativeProduct);
    }

    @Override
    public int insertCreativeProduct(CreativeProduct creativeProduct)
    {
        return creativeProductMapper.insertCreativeProduct(creativeProduct);
    }

    @Override
    public int updateCreativeProduct(CreativeProduct creativeProduct)
    {
        return creativeProductMapper.updateCreativeProduct(creativeProduct);
    }

    @Override
    public int deleteCreativeProductByProductId(Long productId)
    {
        return creativeProductMapper.deleteCreativeProductByProductId(productId);
    }

    @Override
    public int deleteCreativeProductByProductIds(Long[] productIds)
    {
        return creativeProductMapper.deleteCreativeProductByProductIds(productIds);
    }
}
