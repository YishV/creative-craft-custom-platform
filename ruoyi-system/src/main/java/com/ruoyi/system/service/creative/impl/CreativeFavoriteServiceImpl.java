package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.service.creative.ICreativeFavoriteService;

@Service
public class CreativeFavoriteServiceImpl implements ICreativeFavoriteService
{
    @Autowired
    private CreativeFavoriteMapper creativeFavoriteMapper;

    @Override
    public CreativeFavorite selectCreativeFavoriteByFavoriteId(Long favoriteId)
    {
        return creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(favoriteId);
    }

    @Override
    public List<CreativeFavorite> selectCreativeFavoriteList(CreativeFavorite creativeFavorite)
    {
        return creativeFavoriteMapper.selectCreativeFavoriteList(creativeFavorite);
    }

    @Override
    public int insertCreativeFavorite(CreativeFavorite creativeFavorite)
    {
        return creativeFavoriteMapper.insertCreativeFavorite(creativeFavorite);
    }

    @Override
    public int updateCreativeFavorite(CreativeFavorite creativeFavorite)
    {
        return creativeFavoriteMapper.updateCreativeFavorite(creativeFavorite);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteId(Long favoriteId)
    {
        return creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteId(favoriteId);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        return creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteIds(favoriteIds);
    }
}
