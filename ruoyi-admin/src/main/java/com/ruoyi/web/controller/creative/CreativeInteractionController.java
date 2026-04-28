package com.ruoyi.web.controller.creative;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.service.creative.ICreativeInteractionService;

/**
 * 社区交互 Controller
 */
@RestController
@RequestMapping("/portal/interaction")
public class CreativeInteractionController extends BaseController
{
    @Autowired
    private ICreativeInteractionService interactionService;

    /**
     * 查询评论列表
     */
    @GetMapping("/comment/list")
    public TableDataInfo list(CreativeComment comment)
    {
        startPage();
        List<CreativeComment> list = interactionService.selectCommentList(comment);
        return getDataTable(list);
    }

    /**
     * 发表评论
     */
    @PostMapping("/comment")
    public AjaxResult addComment(@RequestBody CreativeComment comment)
    {
        comment.setUserId(getUserId());
        comment.setUserName(getUsername());
        return toAjax(interactionService.insertComment(comment));
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like")
    public AjaxResult toggleLike(@RequestParam String targetType, @RequestParam Long targetId)
    {
        return toAjax(interactionService.toggleLike(targetType, targetId, getUserId()));
    }

    /**
     * 关注/取消关注创作者
     */
    @PostMapping("/follow/{creatorId}")
    public AjaxResult toggleFollow(@PathVariable Long creatorId)
    {
        return toAjax(interactionService.toggleFollow(creatorId, getUserId()));
    }

    /**
     * 记录分享
     */
    @PostMapping("/share")
    public AjaxResult addShare(@RequestParam String targetType, @RequestParam Long targetId, @RequestParam(required = false) String platform)
    {
        return toAjax(interactionService.insertShare(targetType, targetId, getUserId(), platform));
    }

    /**
     * 检查状态 (点赞、关注)
     */
    @GetMapping("/check")
    public AjaxResult checkStatus(@RequestParam String targetType, @RequestParam Long targetId, @RequestParam(required = false) Long creatorId)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("liked", interactionService.isLiked(targetType, targetId, getUserId()));
        if (creatorId != null)
        {
            ajax.put("followed", interactionService.isFollowed(creatorId, getUserId()));
        }
        return ajax;
    }
}
