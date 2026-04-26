package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.service.creative.ICreativeFavoriteService;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeFavoriteServiceImpl implements ICreativeFavoriteService
{
    @Autowired
    private CreativeFavoriteMapper creativeFavoriteMapper;

    @Autowired
    private CreativeDataPermissionService permissionService;

    @Override
    public CreativeFavorite selectCreativeFavoriteByFavoriteId(Long favoriteId)
    {
        CreativeFavorite favorite = requireFavorite(favoriteId);
        permissionService.ensureBuyerOwned(favorite.getUserId());
        return favorite;
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.BUYER, field = "userId")
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
        CreativeFavorite existing = requireFavorite(creativeFavorite.getFavoriteId());
        permissionService.ensureBuyerOwned(existing.getUserId());
        creativeFavorite.setUserId(existing.getUserId());
        return creativeFavoriteMapper.updateCreativeFavorite(creativeFavorite);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteId(Long favoriteId)
    {
        permissionService.ensureBuyerOwned(requireFavorite(favoriteId).getUserId());
        return creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteId(favoriteId);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        if (favoriteIds != null)
        {
            for (Long favoriteId : favoriteIds)
            {
                permissionService.ensureBuyerOwned(requireFavorite(favoriteId).getUserId());
            }
        }
        return creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteIds(favoriteIds);
    }

    private CreativeFavorite requireFavorite(Long favoriteId)
    {
        CreativeFavorite favorite = creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(favoriteId);
        if (favorite == null)
        {
            throw new ServiceException("收藏不存在: " + favoriteId);
        }
        return favorite;
    }
}
