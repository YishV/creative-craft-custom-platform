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
import com.ruoyi.system.domain.creative.CreativeQuote;
import com.ruoyi.system.service.creative.ICreativeQuoteService;

@RestController
@RequestMapping("/creative/quote")
public class CreativeQuoteController extends BaseController
{
    @Autowired
    private ICreativeQuoteService creativeQuoteService;

    @PreAuthorize("@ss.hasPermi('creative:quote:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeQuote creativeQuote)
    {
        startPage();
        List<CreativeQuote> list = creativeQuoteService.selectCreativeQuoteList(creativeQuote);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:quote:query')")
    @GetMapping("/{quoteId}")
    public AjaxResult getInfo(@PathVariable Long quoteId)
    {
        return success(creativeQuoteService.selectCreativeQuoteByQuoteId(quoteId));
    }

    @PreAuthorize("@ss.hasPermi('creative:quote:add')")
    @Log(title = "定制报价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeQuote creativeQuote)
    {
        creativeQuote.setCreateBy(getUsername());
        return toAjax(creativeQuoteService.insertCreativeQuote(creativeQuote));
    }

    @PreAuthorize("@ss.hasPermi('creative:quote:edit')")
    @Log(title = "定制报价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeQuote creativeQuote)
    {
        creativeQuote.setUpdateBy(getUsername());
        return toAjax(creativeQuoteService.updateCreativeQuote(creativeQuote));
    }

    @PreAuthorize("@ss.hasPermi('creative:quote:remove')")
    @Log(title = "定制报价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{quoteIds}")
    public AjaxResult remove(@PathVariable Long[] quoteIds)
    {
        return toAjax(creativeQuoteService.deleteCreativeQuoteByQuoteIds(quoteIds));
    }
}
