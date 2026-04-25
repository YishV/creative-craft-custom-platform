package com.ruoyi.system.service.creative.impl;

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
        ensureBuyerOwned(favorite);
        return favorite;
    }

    @Override
    public List<CreativeFavorite> selectCreativeFavoriteList(CreativeFavorite creativeFavorite)
    {
        if (!permissionService.isAdmin())
        {
            creativeFavorite.setUserId(permissionService.getCurrentUserId());
        }
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
        ensureBuyerOwned(existing);
        creativeFavorite.setUserId(existing.getUserId());
        return creativeFavoriteMapper.updateCreativeFavorite(creativeFavorite);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteId(Long favoriteId)
    {
        ensureBuyerOwned(requireFavorite(favoriteId));
        return creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteId(favoriteId);
    }

    @Override
    public int deleteCreativeFavoriteByFavoriteIds(Long[] favoriteIds)
    {
        if (favoriteIds != null)
        {
            for (Long favoriteId : favoriteIds)
            {
                ensureBuyerOwned(requireFavorite(favoriteId));
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

    private void ensureBuyerOwned(CreativeFavorite favorite)
    {
        if (favorite == null || permissionService.isAdmin())
        {
            return;
        }
        if (!permissionService.getCurrentUserId().equals(favorite.getUserId()))
        {
            throw new ServiceException("无权操作该数据");
        }
    }
}
