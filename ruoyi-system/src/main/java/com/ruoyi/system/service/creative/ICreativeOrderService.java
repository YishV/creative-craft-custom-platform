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

    /**
     * 推进订单状态，校验状态机合法性。
     */
    int transitOrderStatus(Long orderId, String targetStatus, String operator);
}
