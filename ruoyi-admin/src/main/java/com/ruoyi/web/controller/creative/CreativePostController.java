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
import com.ruoyi.system.domain.creative.CreativePost;
import com.ruoyi.system.service.creative.ICreativePostService;

@RestController
@RequestMapping("/creative/post")
public class CreativePostController extends BaseController
{
    @Autowired
    private ICreativePostService creativePostService;

    @PreAuthorize("@ss.hasPermi('creative:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(CreativePost creativePost)
    {
        startPage();
        List<CreativePost> list = creativePostService.selectCreativePostList(creativePost);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('creative:post:query')")
    @GetMapping("/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        return success(creativePostService.selectCreativePostByPostId(postId));
    }

    @PreAuthorize("@ss.hasPermi('creative:post:add')")
    @Log(title = "作品分享", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CreativePost creativePost)
    {
        creativePost.setCreateBy(getUsername());
        return toAjax(creativePostService.insertCreativePost(creativePost));
    }

    @PreAuthorize("@ss.hasPermi('creative:post:edit')")
    @Log(title = "作品分享", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CreativePost creativePost)
    {
        creativePost.setUpdateBy(getUsername());
        return toAjax(creativePostService.updateCreativePost(creativePost));
    }

    @PreAuthorize("@ss.hasPermi('creative:post:remove')")
    @Log(title = "作品分享", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        return toAjax(creativePostService.deleteCreativePostByPostIds(postIds));
    }
}
