package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreativeFavoriteServiceImplTest
{
    @Mock
    private CreativeFavoriteMapper creativeFavoriteMapper;

    @Mock
    private CreativeDataPermissionService permissionService;

    @InjectMocks
    private CreativeFavoriteServiceImpl creativeFavoriteService;

    @Test
    void deleteCreativeFavoriteByFavoriteIdShouldFailWhenFavoriteNotOwnedByCurrentBuyer()
    {
        when(creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(1L)).thenReturn(buildFavorite(1L, 8L));
        doThrow(new ServiceException("无权操作该数据"))
            .when(permissionService).ensureBuyerOwned(eq(8L));

        assertThrows(ServiceException.class, () -> creativeFavoriteService.deleteCreativeFavoriteByFavoriteId(1L));

        verify(creativeFavoriteMapper, never()).deleteCreativeFavoriteByFavoriteId(1L);
    }

    @Test
    void deleteCreativeFavoriteByFavoriteIdShouldDeleteWhenOwnershipPasses()
    {
        when(creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(1L)).thenReturn(buildFavorite(1L, 7L));
        when(creativeFavoriteMapper.deleteCreativeFavoriteByFavoriteId(1L)).thenReturn(1);

        creativeFavoriteService.deleteCreativeFavoriteByFavoriteId(1L);

        verify(permissionService).ensureBuyerOwned(eq(7L));
        verify(creativeFavoriteMapper).deleteCreativeFavoriteByFavoriteId(1L);
    }

    private CreativeFavorite buildFavorite(Long favoriteId, Long userId)
    {
        CreativeFavorite favorite = new CreativeFavorite();
        favorite.setFavoriteId(favoriteId);
        favorite.setUserId(userId);
        favorite.setTargetType("product");
        favorite.setTargetId(9L);
        favorite.setStatus("0");
        return favorite;
    }
}
