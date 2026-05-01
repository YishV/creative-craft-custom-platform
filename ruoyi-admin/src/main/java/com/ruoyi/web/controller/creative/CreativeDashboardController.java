package com.ruoyi.web.controller.creative;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.creative.ICreativeDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/creative/dashboard")
public class CreativeDashboardController extends BaseController
{
    @Autowired
    private ICreativeDashboardService creativeDashboardService;

    @PreAuthorize("@ss.hasPermi('creative:dashboard:view')")
    @GetMapping("/stats")
    public AjaxResult stats()
    {
        return success(creativeDashboardService.getStats());
    }
}
