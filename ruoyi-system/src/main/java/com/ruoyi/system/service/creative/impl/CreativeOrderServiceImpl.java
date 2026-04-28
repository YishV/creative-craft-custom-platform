package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.domain.creative.CreativeProductOrderRequest;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeOrderService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeOrderServiceImpl implements ICreativeOrderService
{
    private static final String STATUS_ON_SHELF = "0";
    private static final String SOURCE_PRODUCT = "product";
    private static final String PAY_UNPAID = "unpaid";
    private static final String PAY_PAID = "paid";

    @Autowired
    private CreativeOrderMapper creativeOrderMapper;

    @Autowired
    private CreativeProductMapper creativeProductMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeOrder selectCreativeOrderByOrderId(Long orderId)
    {
        CreativeOrder order = requireOrder(orderId);
        permissionService.ensureOrderOwned(order.getBuyerId(), order.getSellerId());
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
        permissionService.ensureOrderOwned(existing.getBuyerId(), existing.getSellerId());
        creativeOrder.setBuyerId(existing.getBuyerId());
        creativeOrder.setSellerId(existing.getSellerId());
        return creativeOrderMapper.updateCreativeOrder(creativeOrder);
    }

    @Override
    public int deleteCreativeOrderByOrderId(Long orderId)
    {
        CreativeOrder existing = requireOrder(orderId);
        permissionService.ensureOrderOwned(existing.getBuyerId(), existing.getSellerId());
        return creativeOrderMapper.deleteCreativeOrderByOrderId(orderId);
    }

    @Override
    public int deleteCreativeOrderByOrderIds(Long[] orderIds)
    {
        if (orderIds != null)
        {
            for (Long orderId : orderIds)
            {
                CreativeOrder existing = requireOrder(orderId);
                permissionService.ensureOrderOwned(existing.getBuyerId(), existing.getSellerId());
            }
        }
        return creativeOrderMapper.deleteCreativeOrderByOrderIds(orderIds);
    }

    @Override
    public int transitOrderStatus(Long orderId, String targetStatus, String operator)
    {
        CreativeOrder order = requireOrder(orderId);
        permissionService.ensureOrderOwned(order.getBuyerId(), order.getSellerId());
        ensureActorCanTransit(order, targetStatus);
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

    @Override
    public CreativeOrder createProductOrder(CreativeProductOrderRequest request, String operator)
    {
        validateProductOrderRequest(request);
        CreativeProduct product = creativeProductMapper.selectCreativeProductByProductId(request.getProductId());
        if (product == null || !STATUS_ON_SHELF.equals(product.getStatus()))
        {
            throw new ServiceException("商品不存在或已下架");
        }
        CreativeOrder order = new CreativeOrder();
        order.setOrderNo("CRAFT" + System.currentTimeMillis() + product.getProductId());
        order.setBuyerId(permissionService.getCurrentUserId());
        order.setSellerId(product.getCreatorId());
        order.setOrderAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setOrderStatus(CreativeStatusFlow.Order.CREATED);
        order.setPayStatus(PAY_UNPAID);
        order.setSourceType(SOURCE_PRODUCT);
        order.setSourceId(product.getProductId());
        order.setSourceName(product.getProductName());
        order.setQuantity(request.getQuantity());
        order.setAddressSnapshot(buildAddressSnapshot(request));
        order.setRemark("商品下单：" + product.getProductName());
        order.setCreateBy(operator);
        creativeOrderMapper.insertCreativeOrder(order);
        return order;
    }

    @Override
    public int payOrder(Long orderId, String operator)
    {
        CreativeOrder order = requireOrder(orderId);
        permissionService.ensureBuyerOwned(order.getBuyerId());
        if (PAY_PAID.equals(order.getPayStatus()))
        {
            throw new ServiceException("订单已支付，请勿重复支付");
        }
        order.setPayStatus(PAY_PAID);
        order.setUpdateBy(operator);
        return creativeOrderMapper.updateCreativeOrder(order);
    }

    private void ensureActorCanTransit(CreativeOrder order, String targetStatus)
    {
        if (permissionService.isAdmin())
        {
            return;
        }
        if (CreativeStatusFlow.Order.MAKING.equals(targetStatus)
            || CreativeStatusFlow.Order.SHIPPED.equals(targetStatus))
        {
            Long creatorId = permissionService.getCurrentCreatorIdOrNull();
            if (creatorId == null || !creatorId.equals(order.getSellerId()))
            {
                throw new ServiceException("只有创作者可以执行该订单操作");
            }
        }
        if (CreativeStatusFlow.Order.FINISHED.equals(targetStatus))
        {
            Long userId = permissionService.getCurrentUserId();
            if (userId == null || !userId.equals(order.getBuyerId()))
            {
                throw new ServiceException("只有买家可以执行该订单操作");
            }
        }
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

    private CreativeOrder copyOrderQuery(CreativeOrder source)
    {
        CreativeOrder target = new CreativeOrder();
        if (source == null)
        {
            return target;
        }
        target.setOrderNo(source.getOrderNo());
        target.setOrderStatus(source.getOrderStatus());
        target.setPayStatus(source.getPayStatus());
        target.setRemark(source.getRemark());
        target.setParams(source.getParams());
        return target;
    }

    private void validateProductOrderRequest(CreativeProductOrderRequest request)
    {
        if (request == null || request.getProductId() == null)
        {
            throw new ServiceException("请选择要购买的商品");
        }
        if (request.getQuantity() == null || request.getQuantity() < 1)
        {
            throw new ServiceException("购买数量必须大于 0");
        }
        if (isBlank(request.getReceiverName()) || isBlank(request.getReceiverPhone()) || isBlank(request.getReceiverAddress()))
        {
            throw new ServiceException("请填写完整收货信息");
        }
    }

    private String buildAddressSnapshot(CreativeProductOrderRequest request)
    {
        return request.getReceiverName() + " / " + request.getReceiverPhone() + " / " + request.getReceiverAddress();
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
