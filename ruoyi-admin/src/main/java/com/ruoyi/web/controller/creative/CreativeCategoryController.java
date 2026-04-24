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
import com.ruoyi.system.domain.creative.CreativeCategory;
import com.ruoyi.system.service.creative.ICreativeCategoryService;

@RestController
@RequestMapping("/creative/category")
public class CreativeCategoryController extends BaseController
{
    @Autowired
    private ICreativeCategoryService creativeCategoryService;

    @PreAuthorize("@ss.hasPermi('creative:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeCategory creativeCategory)
    {
        startPage();
        List<CreativeCategory> list = creativeCategoryService.selectCreativeCategoryList(creativeCategory);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return success(creativeCategoryService.selectCreativeCategoryByCategoryId(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('creative:category:add')")
    @Log(title = "文创分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeCategory creativeCategory)
    {
        creativeCategory.setCreateBy(getUsername());
        return toAjax(creativeCategoryService.insertCreativeCategory(creativeCategory));
    }

    @PreAuthorize("@ss.hasPermi('creative:category:edit')")
    @Log(title = "文创分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeCategory creativeCategory)
    {
        creativeCategory.setUpdateBy(getUsername());
        return toAjax(creativeCategoryService.updateCreativeCategory(creativeCategory));
    }

    @PreAuthorize("@ss.hasPermi('creative:category:remove')")
    @Log(title = "文创分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(creativeCategoryService.deleteCreativeCategoryByCategoryIds(categoryIds));
    }
}
