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
import com.ruoyi.system.domain.creative.CreativeDemand;
import com.ruoyi.system.service.creative.ICreativeDemandService;

@RestController
@RequestMapping("/creative/demand")
public class CreativeDemandController extends BaseController
{
    @Autowired
    private ICreativeDemandService creativeDemandService;

    @PreAuthorize("@ss.hasPermi('creative:demand:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeDemand creativeDemand)
    {
        startPage();
        List<CreativeDemand> list = creativeDemandService.selectCreativeDemandList(creativeDemand);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:demand:query')")
    @GetMapping("/{demandId}")
    public AjaxResult getInfo(@PathVariable Long demandId)
    {
        return success(creativeDemandService.selectCreativeDemandByDemandId(demandId));
    }

    @PreAuthorize("@ss.hasPermi('creative:demand:add')")
    @Log(title = "定制需求", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeDemand creativeDemand)
    {
        if (!SecurityUtils.isAdmin())
        {
            creativeDemand.setUserId(getUserId());
        }
        creativeDemand.setCreateBy(getUsername());
        return toAjax(creativeDemandService.insertCreativeDemand(creativeDemand));
    }

    @PreAuthorize("@ss.hasPermi('creative:demand:edit')")
    @Log(title = "定制需求", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeDemand creativeDemand)
    {
        creativeDemand.setUpdateBy(getUsername());
        return toAjax(creativeDemandService.updateCreativeDemand(creativeDemand));
    }

    @PreAuthorize("@ss.hasPermi('creative:demand:remove')")
    @Log(title = "定制需求", businessType = BusinessType.DELETE)
    @DeleteMapping("/{demandIds}")
    public AjaxResult remove(@PathVariable Long[] demandIds)
    {
        return toAjax(creativeDemandService.deleteCreativeDemandByDemandIds(demandIds));
    }
}
