package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.service.creative.ICreativeOrderService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeOrderServiceImpl implements ICreativeOrderService
{
    @Autowired
    private CreativeOrderMapper creativeOrderMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeOrder selectCreativeOrderByOrderId(Long orderId)
    {
        CreativeOrder order = requireOrder(orderId);
        ensureOrderAccessible(order);
        return order;
    }

    @Override
    public List<CreativeOrder> selectCreativeOrderList(CreativeOrder creativeOrder)
    {
        if (permissionService.isAdmin())
        {
            return creativeOrderMapper.selectCreativeOrderList(creativeOrder);
        }

        Map<Long, CreativeOrder> merged = new LinkedHashMap<>();

        CreativeOrder buyerQuery = copyOrderQuery(creativeOrder);
        buyerQuery.setBuyerId(permissionService.getCurrentUserId());
        for (CreativeOrder order : creativeOrderMapper.selectCreativeOrderList(buyerQuery))
        {
            merged.put(order.getOrderId(), order);
        }

        Long creatorId = permissionService.getCurrentCreatorIdOrNull();
        if (creatorId != null)
        {
            CreativeOrder sellerQuery = copyOrderQuery(creativeOrder);
            sellerQuery.setSellerId(creatorId);
            for (CreativeOrder order : creativeOrderMapper.selectCreativeOrderList(sellerQuery))
            {
                merged.put(order.getOrderId(), order);
            }
        }

        return new ArrayList<>(merged.values());
    }

    @Override
    public int insertCreativeOrder(CreativeOrder creativeOrder)
    {
        return creativeOrderMapper.insertCreativeOrder(creativeOrder);
    }

    @Override
    public int updateCreativeOrder(CreativeOrder creativeOrder)
    {
        CreativeOrder existing = requireOrder(creativeOrder.getOrderId());
        ensureOrderAccessible(existing);
        creativeOrder.setBuyerId(existing.getBuyerId());
        creativeOrder.setSellerId(existing.getSellerId());
        return creativeOrderMapper.updateCreativeOrder(creativeOrder);
    }

    @Override
    public int deleteCreativeOrderByOrderId(Long orderId)
    {
        ensureOrderAccessible(requireOrder(orderId));
        return creativeOrderMapper.deleteCreativeOrderByOrderId(orderId);
    }

    @Override
    public int deleteCreativeOrderByOrderIds(Long[] orderIds)
    {
        if (orderIds != null)
        {
            for (Long orderId : orderIds)
            {
                ensureOrderAccessible(requireOrder(orderId));
            }
        }
        return creativeOrderMapper.deleteCreativeOrderByOrderIds(orderIds);
    }

    @Override
    public int transitOrderStatus(Long orderId, String targetStatus, String operator)
    {
        CreativeOrder order = requireOrder(orderId);
        ensureOrderAccessible(order);
        try
        {
            CreativeStatusFlow.ensureOrderTransition(order.getOrderStatus(), targetStatus);
        }
        catch (IllegalStateException e)
        {
            throw new ServiceException(e.getMessage());
        }
        order.setOrderStatus(targetStatus);
        order.setUpdateBy(operator);
        return creativeOrderMapper.updateCreativeOrder(order);
    }

    private CreativeOrder requireOrder(Long orderId)
    {
        CreativeOrder order = creativeOrderMapper.selectCreativeOrderByOrderId(orderId);
        if (order == null)
        {
            throw new ServiceException("订单不存在: " + orderId);
        }
        return order;
    }

    private void ensureOrderAccessible(CreativeOrder order)
    {
        if (order == null || permissionService.isAdmin())
        {
            return;
        }
        Long currentUserId = permissionService.getCurrentUserId();
        Long currentCreatorId = permissionService.getCurrentCreatorIdOrNull();
        if (currentUserId.equals(order.getBuyerId()))
        {
            return;
        }
        if (currentCreatorId != null && currentCreatorId.equals(order.getSellerId()))
        {
            return;
        }
        throw new ServiceException("无权操作该数据");
    }

    private CreativeOrder copyOrderQuery(CreativeOrder source)
    {
        CreativeOrder target = new CreativeOrder();
        if (source == null)
        {
            return target;
        }
        target.setOrderNo(source.getOrderNo());
        target.setOrderStatus(source.getOrderStatus());
        target.setRemark(source.getRemark());
        target.setParams(source.getParams());
        return target;
    }
}
