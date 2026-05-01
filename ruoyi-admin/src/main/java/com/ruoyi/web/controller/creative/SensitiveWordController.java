package com.ruoyi.web.controller.creative;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.creative.SensitiveCheckResult;
import com.ruoyi.system.domain.creative.SensitiveWord;
import com.ruoyi.system.service.creative.ISensitiveWordService;
import java.util.List;
import java.util.Map;
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

@RestController
@RequestMapping("/creative/sensitive")
public class SensitiveWordController extends BaseController
{
    @Autowired
    private ISensitiveWordService sensitiveWordService;

    @PreAuthorize("@ss.hasPermi('creative:sensitive:list')")
    @GetMapping("/list")
    public TableDataInfo list(SensitiveWord query)
    {
        startPage();
        List<SensitiveWord> list = sensitiveWordService.selectSensitiveWordList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:sensitive:query')")
    @GetMapping("/{wordId}")
    public AjaxResult getInfo(@PathVariable Long wordId)
    {
        return success(sensitiveWordService.selectSensitiveWordById(wordId));
    }

    @PreAuthorize("@ss.hasPermi('creative:sensitive:add')")
    @Log(title = "敏感词", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SensitiveWord sensitiveWord)
    {
        sensitiveWord.setCreateBy(getUsername());
        return toAjax(sensitiveWordService.insertSensitiveWord(sensitiveWord));
    }

    @PreAuthorize("@ss.hasPermi('creative:sensitive:edit')")
    @Log(title = "敏感词", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SensitiveWord sensitiveWord)
    {
        sensitiveWord.setUpdateBy(getUsername());
        return toAjax(sensitiveWordService.updateSensitiveWord(sensitiveWord));
    }

    @PreAuthorize("@ss.hasPermi('creative:sensitive:remove')")
    @Log(title = "敏感词", businessType = BusinessType.DELETE)
    @DeleteMapping("/{wordIds}")
    public AjaxResult remove(@PathVariable Long[] wordIds)
    {
        return toAjax(sensitiveWordService.deleteSensitiveWordByIds(wordIds));
    }

    /** 在线检测：传 text 看哪几条敏感词命中 */
    @PreAuthorize("@ss.hasPermi('creative:sensitive:list')")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody Map<String, String> body)
    {
        String text = body == null ? null : body.get("text");
        SensitiveCheckResult r = sensitiveWordService.check(text);
        return success(r);
    }

    /** 强制重建 DFA 树 */
    @PreAuthorize("@ss.hasPermi('creative:sensitive:edit')")
    @PostMapping("/reload")
    public AjaxResult reload()
    {
        sensitiveWordService.reload();
        return success();
    }
}
