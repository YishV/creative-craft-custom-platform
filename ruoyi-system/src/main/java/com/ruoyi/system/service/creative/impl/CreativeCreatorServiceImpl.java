package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.service.creative.ICreativeCreatorService;

@Service
public class CreativeCreatorServiceImpl implements ICreativeCreatorService
{
    @Autowired
    private CreativeCreatorMapper creativeCreatorMapper;

    @Override
    public CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId)
    {
        return creativeCreatorMapper.selectCreativeCreatorByCreatorId(creatorId);
    }

    @Override
    public List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator)
    {
        return creativeCreatorMapper.selectCreativeCreatorList(creativeCreator);
    }

    @Override
    public int insertCreativeCreator(CreativeCreator creativeCreator)
    {
        return creativeCreatorMapper.insertCreativeCreator(creativeCreator);
    }

    @Override
    public int updateCreativeCreator(CreativeCreator creativeCreator)
    {
        return creativeCreatorMapper.updateCreativeCreator(creativeCreator);
    }

    @Override
    public int deleteCreativeCreatorByCreatorId(Long creatorId)
    {
        return creativeCreatorMapper.deleteCreativeCreatorByCreatorId(creatorId);
    }

    @Override
    public int deleteCreativeCreatorByCreatorIds(Long[] creatorIds)
    {
        return creativeCreatorMapper.deleteCreativeCreatorByCreatorIds(creatorIds);
    }
}
