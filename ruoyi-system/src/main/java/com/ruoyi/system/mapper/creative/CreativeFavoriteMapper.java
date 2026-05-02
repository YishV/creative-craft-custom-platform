package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeFavorite;

public interface CreativeFavoriteMapper
{
    CreativeFavorite selectCreativeFavoriteByFavoriteId(Long favoriteId);

    List<CreativeFavorite> selectCreativeFavoriteList(CreativeFavorite creativeFavorite);

    int insertCreativeFavorite(CreativeFavorite creativeFavorite);

    int updateCreativeFavorite(CreativeFavorite creativeFavorite);

    int deleteCreativeFavoriteByFavoriteId(Long favoriteId);

    int deleteCreativeFavoriteByFavoriteIds(Long[] favoriteIds);

    int deleteByUserAndTarget(@org.apache.ibatis.annotations.Param("userId") Long userId,
                              @org.apache.ibatis.annotations.Param("targetType") String targetType,
                              @org.apache.ibatis.annotations.Param("targetId") Long targetId);

    int countByUserAndTarget(@org.apache.ibatis.annotations.Param("userId") Long userId,
                             @org.apache.ibatis.annotations.Param("targetType") String targetType,
                             @org.apache.ibatis.annotations.Param("targetId") Long targetId);
}
