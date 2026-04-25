package com.ruoyi.web.controller.creative;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.service.creative.ICreativeFavoriteService;

@RestController
@RequestMapping("/creative/favorite")
public class CreativeFavoriteController extends BaseController
{
    @Autowired
    private ICreativeFavoriteService creativeFavoriteService;

    @PreAuthorize("@ss.hasPermi('creative:favorite:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeFavorite creativeFavorite)
    {
        startPage();
        List<CreativeFavorite> list = creativeFavoriteService.selectCreativeFavoriteList(creativeFavorite);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:favorite:query')")
    @GetMapping("/{favoriteId}")
    public AjaxResult getInfo(@PathVariable Long favoriteId)
    {
        return success(creativeFavoriteService.selectCreativeFavoriteByFavoriteId(favoriteId));
    }

    @PreAuthorize("@ss.hasPermi('creative:favorite:add')")
    @Log(title = "收藏关注", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeFavorite creativeFavorite)
    {
        if (!SecurityUtils.isAdmin())
        {
            creativeFavorite.setUserId(getUserId());
        }
        creativeFavorite.setCreateBy(getUsername());
        return toAjax(creativeFavoriteService.insertCreativeFavorite(creativeFavorite));
    }

    @PreAuthorize("@ss.hasPermi('creative:favorite:edit')")
    @Log(title = "收藏关注", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeFavorite creativeFavorite)
    {
        creativeFavorite.setUpdateBy(getUsername());
        return toAjax(creativeFavoriteService.updateCreativeFavorite(creativeFavorite));
    }

    @PreAuthorize("@ss.hasPermi('creative:favorite:remove')")
    @Log(title = "收藏关注", businessType = BusinessType.DELETE)
    @DeleteMapping("/{favoriteIds}")
    public AjaxResult remove(@PathVariable Long[] favoriteIds)
    {
        return toAjax(creativeFavoriteService.deleteCreativeFavoriteByFavoriteIds(favoriteIds));
    }
}
