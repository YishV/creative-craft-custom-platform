package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeOrder;

public interface ICreativeOrderService
{
    CreativeOrder selectCreativeOrderByOrderId(Long orderId);

    List<CreativeOrder> selectCreativeOrderList(CreativeOrder creativeOrder);

    int insertCreativeOrder(CreativeOrder creativeOrder);

    int updateCreativeOrder(CreativeOrder creativeOrder);

    int deleteCreativeOrderByOrderId(Long orderId);

    int deleteCreativeOrderByOrderIds(Long[] orderIds);
}
