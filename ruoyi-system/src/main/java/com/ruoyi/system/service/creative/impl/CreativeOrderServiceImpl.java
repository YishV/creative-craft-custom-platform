package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.service.creative.ICreativeOrderService;

@Service
public class CreativeOrderServiceImpl implements ICreativeOrderService
{
    @Autowired
    private CreativeOrderMapper creativeOrderMapper;

    @Override
    public CreativeOrder selectCreativeOrderByOrderId(Long orderId)
    {
        return creativeOrderMapper.selectCreativeOrderByOrderId(orderId);
    }

    @Override
    public List<CreativeOrder> selectCreativeOrderList(CreativeOrder creativeOrder)
    {
        return creativeOrderMapper.selectCreativeOrderList(creativeOrder);
    }

    @Override
    public int insertCreativeOrder(CreativeOrder creativeOrder)
    {
        return creativeOrderMapper.insertCreativeOrder(creativeOrder);
    }

    @Override
    public int updateCreativeOrder(CreativeOrder creativeOrder)
    {
        return creativeOrderMapper.updateCreativeOrder(creativeOrder);
    }

    @Override
    public int deleteCreativeOrderByOrderId(Long orderId)
    {
        return creativeOrderMapper.deleteCreativeOrderByOrderId(orderId);
    }

    @Override
    public int deleteCreativeOrderByOrderIds(Long[] orderIds)
    {
        return creativeOrderMapper.deleteCreativeOrderByOrderIds(orderIds);
    }
}
