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
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.service.creative.ICreativeCreatorService;

@RestController
@RequestMapping("/creative/creator")
public class CreativeCreatorController extends BaseController
{
    @Autowired
    private ICreativeCreatorService creativeCreatorService;

    @PreAuthorize("@ss.hasPermi('creative:creator:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeCreator creativeCreator)
    {
        startPage();
        List<CreativeCreator> list = creativeCreatorService.selectCreativeCreatorList(creativeCreator);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:creator:query')")
    @GetMapping("/{creatorId}")
    public AjaxResult getInfo(@PathVariable Long creatorId)
    {
        return success(creativeCreatorService.selectCreativeCreatorByCreatorId(creatorId));
    }

    @PreAuthorize("@ss.hasPermi('creative:creator:add')")
    @Log(title = "创作者管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeCreator creativeCreator)
    {
        creativeCreator.setCreateBy(getUsername());
        return toAjax(creativeCreatorService.insertCreativeCreator(creativeCreator));
    }

    @PreAuthorize("@ss.hasPermi('creative:creator:edit')")
    @Log(title = "创作者管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeCreator creativeCreator)
    {
        creativeCreator.setUpdateBy(getUsername());
        return toAjax(creativeCreatorService.updateCreativeCreator(creativeCreator));
    }

    @PreAuthorize("@ss.hasPermi('creative:creator:remove')")
    @Log(title = "创作者管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{creatorIds}")
    public AjaxResult remove(@PathVariable Long[] creatorIds)
    {
        return toAjax(creativeCreatorService.deleteCreativeCreatorByCreatorIds(creatorIds));
    }
}
