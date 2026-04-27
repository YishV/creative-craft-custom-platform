package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeOrder;
import com.ruoyi.system.domain.creative.CreativeQuote;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeQuoteMapper;
import com.ruoyi.system.service.creative.ICreativeQuoteService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreativeQuoteServiceImpl implements ICreativeQuoteService
{
    @Autowired
    private CreativeQuoteMapper creativeQuoteMapper;

    @Autowired
    private CreativeDemandMapper creativeDemandMapper;

    @Autowired
    private CreativeOrderMapper creativeOrderMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeQuote selectCreativeQuoteByQuoteId(Long quoteId)
    {
        CreativeQuote quote = requireQuote(quoteId);
        permissionService.ensureCreatorOwned(quote.getCreatorId());
        return quote;
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.CREATOR, field = "creatorId")
    public List<CreativeQuote> selectCreativeQuoteList(CreativeQuote creativeQuote)
    {
        return creativeQuoteMapper.selectCreativeQuoteList(creativeQuote);
    }

    @Override
    public int insertCreativeQuote(CreativeQuote creativeQuote)
    {
        if (!permissionService.isAdmin())
        {
            creativeQuote.setCreatorId(permissionService.requireCurrentCreatorId());
        }
        if (creativeQuote.getQuoteStatus() == null || creativeQuote.getQuoteStatus().isEmpty())
        {
            creativeQuote.setQuoteStatus(CreativeStatusFlow.Quote.PENDING);
        }
        if (creativeQuote.getDemandId() != null)
        {
            CreativeDemand demand = creativeDemandMapper.selectCreativeDemandByDemandId(creativeQuote.getDemandId());
            ensureDemandOpenForQuote(creativeQuote.getDemandId(), demand);
            if (CreativeStatusFlow.Demand.PUBLISHED.equals(demand.getDemandStatus()))
            {
                demand.setDemandStatus(CreativeStatusFlow.Demand.QUOTING);
                demand.setUpdateBy(creativeQuote.getCreateBy());
                creativeDemandMapper.updateCreativeDemand(demand);
            }
        }
        else
        {
            throw new ServiceException("报价对应的需求不能为空");
        }
        return creativeQuoteMapper.insertCreativeQuote(creativeQuote);
    }

    @Override
    public int updateCreativeQuote(CreativeQuote creativeQuote)
    {
        CreativeQuote existing = requireQuote(creativeQuote.getQuoteId());
        permissionService.ensureCreatorOwned(existing.getCreatorId());
        creativeQuote.setCreatorId(existing.getCreatorId());
        return creativeQuoteMapper.updateCreativeQuote(creativeQuote);
    }

    @Override
    public int deleteCreativeQuoteByQuoteId(Long quoteId)
    {
        permissionService.ensureCreatorOwned(requireQuote(quoteId).getCreatorId());
        return creativeQuoteMapper.deleteCreativeQuoteByQuoteId(quoteId);
    }

    @Override
    public int deleteCreativeQuoteByQuoteIds(Long[] quoteIds)
    {
        if (quoteIds != null)
        {
            for (Long quoteId : quoteIds)
            {
                permissionService.ensureCreatorOwned(requireQuote(quoteId).getCreatorId());
            }
        }
        return creativeQuoteMapper.deleteCreativeQuoteByQuoteIds(quoteIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreativeOrder selectQuoteAndGenerateOrder(Long quoteId, String operator)
    {
        CreativeQuote quote = requireQuote(quoteId);
        if (!CreativeStatusFlow.Quote.PENDING.equals(quote.getQuoteStatus()))
        {
            throw new ServiceException("只有待确认状态的报价可以被选中，当前状态: " + quote.getQuoteStatus());
        }

        CreativeDemand demand = creativeDemandMapper.selectCreativeDemandByDemandId(quote.getDemandId());
        if (demand == null)
        {
            throw new ServiceException("报价对应的需求不存在: " + quote.getDemandId());
        }
        permissionService.ensureBuyerOwned(demand.getUserId());
        try
        {
            CreativeStatusFlow.ensureDemandTransition(demand.getDemandStatus(), CreativeStatusFlow.Demand.SELECTED);
        }
        catch (IllegalStateException e)
        {
            throw new ServiceException(e.getMessage());
        }

        quote.setQuoteStatus(CreativeStatusFlow.Quote.SELECTED);
        quote.setUpdateBy(operator);
        creativeQuoteMapper.updateCreativeQuote(quote);

        creativeQuoteMapper.rejectOtherQuotesByDemand(quote.getDemandId(), quote.getQuoteId(),
            CreativeStatusFlow.Quote.REJECTED, operator);

        demand.setDemandStatus(CreativeStatusFlow.Demand.SELECTED);
        demand.setUpdateBy(operator);
        creativeDemandMapper.updateCreativeDemand(demand);

        CreativeOrder order = new CreativeOrder();
        order.setOrderNo("CRAFT" + System.currentTimeMillis() + quote.getQuoteId());
        order.setBuyerId(demand.getUserId());
        order.setSellerId(quote.getCreatorId());
        order.setOrderAmount(quote.getQuoteAmount());
        order.setOrderStatus(CreativeStatusFlow.Order.CREATED);
        order.setRemark("由需求#" + demand.getDemandId() + " 报价#" + quote.getQuoteId() + " 自动生成");
        order.setCreateBy(operator);
        creativeOrderMapper.insertCreativeOrder(order);

        return order;
    }

    private void ensureDemandOpenForQuote(Long demandId, CreativeDemand demand)
    {
        if (demand == null)
        {
            throw new ServiceException("报价对应的需求不存在: " + demandId);
        }
        String status = demand.getDemandStatus();
        if (!CreativeStatusFlow.Demand.PUBLISHED.equals(status)
            && !CreativeStatusFlow.Demand.QUOTING.equals(status))
        {
            throw new ServiceException("当前需求状态不允许报价: " + status);
        }
    }

    private CreativeQuote requireQuote(Long quoteId)
    {
        CreativeQuote quote = creativeQuoteMapper.selectCreativeQuoteByQuoteId(quoteId);
        if (quote == null)
        {
            throw new ServiceException("报价不存在: " + quoteId);
        }
        return quote;
    }
}
