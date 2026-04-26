package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.domain.creative.CreativeStatusFlow;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.service.creative.ICreativeDemandService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeDemandServiceImpl implements ICreativeDemandService
{
    @Autowired
    private CreativeDemandMapper creativeDemandMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeDemand selectCreativeDemandByDemandId(Long demandId)
    {
        CreativeDemand demand = requireDemand(demandId);
        permissionService.ensureBuyerOwned(demand.getUserId());
        return demand;
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.BUYER, field = "userId")
    public List<CreativeDemand> selectCreativeDemandList(CreativeDemand creativeDemand)
    {
        return creativeDemandMapper.selectCreativeDemandList(creativeDemand);
    }

    @Override
    public int insertCreativeDemand(CreativeDemand creativeDemand)
    {
        return creativeDemandMapper.insertCreativeDemand(creativeDemand);
    }

    @Override
    public int updateCreativeDemand(CreativeDemand creativeDemand)
    {
        CreativeDemand existing = requireDemand(creativeDemand.getDemandId());
        permissionService.ensureBuyerOwned(existing.getUserId());
        creativeDemand.setUserId(existing.getUserId());
        return creativeDemandMapper.updateCreativeDemand(creativeDemand);
    }

    @Override
    public int deleteCreativeDemandByDemandId(Long demandId)
    {
        permissionService.ensureBuyerOwned(requireDemand(demandId).getUserId());
        return creativeDemandMapper.deleteCreativeDemandByDemandId(demandId);
    }

    @Override
    public int deleteCreativeDemandByDemandIds(Long[] demandIds)
    {
        if (demandIds != null)
        {
            for (Long demandId : demandIds)
            {
                permissionService.ensureBuyerOwned(requireDemand(demandId).getUserId());
            }
        }
        return creativeDemandMapper.deleteCreativeDemandByDemandIds(demandIds);
    }

    @Override
    public int transitDemandStatus(Long demandId, String targetStatus, String operator)
    {
        CreativeDemand demand = requireDemand(demandId);
        permissionService.ensureBuyerOwned(demand.getUserId());
        try
        {
            CreativeStatusFlow.ensureDemandTransition(demand.getDemandStatus(), targetStatus);
        }
        catch (IllegalStateException e)
        {
            throw new ServiceException(e.getMessage());
        }
        demand.setDemandStatus(targetStatus);
        demand.setUpdateBy(operator);
        return creativeDemandMapper.updateCreativeDemand(demand);
    }

    private CreativeDemand requireDemand(Long demandId)
    {
        CreativeDemand demand = creativeDemandMapper.selectCreativeDemandByDemandId(demandId);
        if (demand == null)
        {
            throw new ServiceException("需求不存在: " + demandId);
        }
        return demand;
    }
}
