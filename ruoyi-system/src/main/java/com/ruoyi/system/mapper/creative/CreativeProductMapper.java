package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeProduct;

public interface CreativeProductMapper
{
    CreativeProduct selectCreativeProductByProductId(Long productId);

    List<CreativeProduct> selectCreativeProductList(CreativeProduct creativeProduct);

    int insertCreativeProduct(CreativeProduct creativeProduct);

    int updateCreativeProduct(CreativeProduct creativeProduct);

    int deleteCreativeProductByProductId(Long productId);

    int deleteCreativeProductByProductIds(Long[] productIds);
}
