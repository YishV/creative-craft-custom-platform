package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.service.creative.ICreativeDemandService;

@Service
public class CreativeDemandServiceImpl implements ICreativeDemandService
{
    @Autowired
    private CreativeDemandMapper creativeDemandMapper;

    @Override
    public CreativeDemand selectCreativeDemandByDemandId(Long demandId)
    {
        return creativeDemandMapper.selectCreativeDemandByDemandId(demandId);
    }

    @Override
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
        return creativeDemandMapper.updateCreativeDemand(creativeDemand);
    }

    @Override
    public int deleteCreativeDemandByDemandId(Long demandId)
    {
        return creativeDemandMapper.deleteCreativeDemandByDemandId(demandId);
    }

    @Override
    public int deleteCreativeDemandByDemandIds(Long[] demandIds)
    {
        return creativeDemandMapper.deleteCreativeDemandByDemandIds(demandIds);
    }
}
