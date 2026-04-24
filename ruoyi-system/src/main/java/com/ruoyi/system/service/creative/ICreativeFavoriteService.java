package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeFavorite;

public interface ICreativeFavoriteService
{
    CreativeFavorite selectCreativeFavoriteByFavoriteId(Long favoriteId);

    List<CreativeFavorite> selectCreativeFavoriteList(CreativeFavorite creativeFavorite);

    int insertCreativeFavorite(CreativeFavorite creativeFavorite);

    int updateCreativeFavorite(CreativeFavorite creativeFavorite);

    int deleteCreativeFavoriteByFavoriteId(Long favoriteId);

    int deleteCreativeFavoriteByFavoriteIds(Long[] favoriteIds);
}
