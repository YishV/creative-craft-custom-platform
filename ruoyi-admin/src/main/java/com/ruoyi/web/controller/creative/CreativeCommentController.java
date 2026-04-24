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
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.service.creative.ICreativeCommentService;

@RestController
@RequestMapping("/creative/comment")
public class CreativeCommentController extends BaseController
{
    @Autowired
    private ICreativeCommentService creativeCommentService;

    @PreAuthorize("@ss.hasPermi('creative:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativeComment creativeComment)
    {
        startPage();
        List<CreativeComment> list = creativeCommentService.selectCreativeCommentList(creativeComment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:comment:query')")
    @GetMapping("/{commentId}")
    public AjaxResult getInfo(@PathVariable Long commentId)
    {
        return success(creativeCommentService.selectCreativeCommentByCommentId(commentId));
    }

    @PreAuthorize("@ss.hasPermi('creative:comment:add')")
    @Log(title = "评论互动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativeComment creativeComment)
    {
        creativeComment.setCreateBy(getUsername());
        return toAjax(creativeCommentService.insertCreativeComment(creativeComment));
    }

    @PreAuthorize("@ss.hasPermi('creative:comment:edit')")
    @Log(title = "评论互动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativeComment creativeComment)
    {
        creativeComment.setUpdateBy(getUsername());
        return toAjax(creativeCommentService.updateCreativeComment(creativeComment));
    }

    @PreAuthorize("@ss.hasPermi('creative:comment:remove')")
    @Log(title = "评论互动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(creativeCommentService.deleteCreativeCommentByCommentIds(commentIds));
    }
}
