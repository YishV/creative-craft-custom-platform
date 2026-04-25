package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeProduct;

public interface ICreativeProductService
{
    CreativeProduct selectCreativeProductByProductId(Long productId);

    List<CreativeProduct> selectCreativeProductList(CreativeProduct creativeProduct);

    int insertCreativeProduct(CreativeProduct creativeProduct);

    int updateCreativeProduct(CreativeProduct creativeProduct);

    int putOnShelf(Long productId, String operator);

    int takeOffShelf(Long productId, String operator);

    int deleteCreativeProductByProductId(Long productId);

    int deleteCreativeProductByProductIds(Long[] productIds);
}
