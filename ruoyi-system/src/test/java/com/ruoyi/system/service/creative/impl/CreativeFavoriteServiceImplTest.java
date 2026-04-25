package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.service.creative.support.CreativeDataPermissionService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void selectCreativeFavoriteListShouldScopeToCurrentBuyer()
    {
        CreativeFavorite query = new CreativeFavorite();
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(7L);
        when(creativeFavoriteMapper.selectCreativeFavoriteList(any(CreativeFavorite.class))).thenReturn(Collections.emptyList());

        creativeFavoriteService.selectCreativeFavoriteList(query);

        ArgumentCaptor<CreativeFavorite> captor = ArgumentCaptor.forClass(CreativeFavorite.class);
        verify(creativeFavoriteMapper).selectCreativeFavoriteList(captor.capture());
        assertEquals(7L, captor.getValue().getUserId());
    }

    @Test
    void deleteCreativeFavoriteByFavoriteIdShouldFailWhenFavoriteNotOwnedByCurrentBuyer()
    {
        when(creativeFavoriteMapper.selectCreativeFavoriteByFavoriteId(1L)).thenReturn(buildFavorite(1L, 8L));
        when(permissionService.isAdmin()).thenReturn(false);
        when(permissionService.getCurrentUserId()).thenReturn(7L);

        assertThrows(ServiceException.class, () -> creativeFavoriteService.deleteCreativeFavoriteByFavoriteId(1L));

        verify(creativeFavoriteMapper, never()).deleteCreativeFavoriteByFavoriteId(1L);
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
