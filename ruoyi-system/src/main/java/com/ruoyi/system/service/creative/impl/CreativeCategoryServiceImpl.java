package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeCategory;
import com.ruoyi.system.mapper.creative.CreativeCategoryMapper;
import com.ruoyi.system.service.creative.ICreativeCategoryService;

@Service
public class CreativeCategoryServiceImpl implements ICreativeCategoryService
{
    @Autowired
    private CreativeCategoryMapper creativeCategoryMapper;

    @Override
    public CreativeCategory selectCreativeCategoryByCategoryId(Long categoryId)
    {
        return creativeCategoryMapper.selectCreativeCategoryByCategoryId(categoryId);
    }

    @Override
    public List<CreativeCategory> selectCreativeCategoryList(CreativeCategory creativeCategory)
    {
        return creativeCategoryMapper.selectCreativeCategoryList(creativeCategory);
    }

    @Override
    public int insertCreativeCategory(CreativeCategory creativeCategory)
    {
        return creativeCategoryMapper.insertCreativeCategory(creativeCategory);
    }

    @Override
    public int updateCreativeCategory(CreativeCategory creativeCategory)
    {
        return creativeCategoryMapper.updateCreativeCategory(creativeCategory);
    }

    @Override
    public int deleteCreativeCategoryByCategoryId(Long categoryId)
    {
        return creativeCategoryMapper.deleteCreativeCategoryByCategoryId(categoryId);
    }

    @Override
    public int deleteCreativeCategoryByCategoryIds(Long[] categoryIds)
    {
        return creativeCategoryMapper.deleteCreativeCategoryByCategoryIds(categoryIds);
    }
}
