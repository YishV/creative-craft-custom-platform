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

    /** 创作者提交审核（草稿/驳回 -> pending） */
    int submitAudit(Long productId, String operator);

    /** 管理员审核通过 */
    int approveAudit(Long productId, String remark, String operator);

    /** 管理员审核驳回 */
    int rejectAudit(Long productId, String remark, String operator);

    int deleteCreativeProductByProductId(Long productId);

    int deleteCreativeProductByProductIds(Long[] productIds);
}
