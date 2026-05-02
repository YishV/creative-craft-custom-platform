package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.domain.creative.CreativeFavorite;
import com.ruoyi.system.mapper.creative.CreativeFavoriteMapper;
import com.ruoyi.system.mapper.creative.CreativeInteractionMapper;
import com.ruoyi.system.service.creative.ICreativeInteractionService;
import com.ruoyi.system.service.creative.ISensitiveWordService;

/**
 * 社区交互 Service 业务层处理
 */
@Service
public class CreativeInteractionServiceImpl implements ICreativeInteractionService
{
    @Autowired
    private CreativeInteractionMapper interactionMapper;

    @Autowired
    private ISensitiveWordService sensitiveWordService;

    @Autowired
    private CreativeFavoriteMapper favoriteMapper;

    @Override
    public List<CreativeComment> selectCommentList(CreativeComment comment)
    {
        return interactionMapper.selectCommentList(comment);
    }

    @Override
    public int insertComment(CreativeComment comment)
    {
        sensitiveWordService.enforceClean(comment.getContent(), "评论内容");
        return interactionMapper.insertComment(comment);
    }

    @Override
    public int deleteCommentById(Long commentId)
    {
        return interactionMapper.deleteCommentById(commentId);
    }

    @Override
    public int toggleLike(String targetType, Long targetId, Long userId)
    {
        if (interactionMapper.checkIsLiked(targetType, targetId, userId) > 0)
        {
            return interactionMapper.deleteLike(targetType, targetId, userId);
        }
        else
        {
            return interactionMapper.insertLike(targetType, targetId, userId);
        }
    }

    @Override
    public boolean isLiked(String targetType, Long targetId, Long userId)
    {
        return userId != null && interactionMapper.checkIsLiked(targetType, targetId, userId) > 0;
    }

    @Override
    public int toggleFollow(Long creatorId, Long userId)
    {
        if (interactionMapper.checkIsFollowed(creatorId, userId) > 0)
        {
            favoriteMapper.deleteByUserAndTarget(userId, "creator", creatorId);
            return interactionMapper.deleteFollow(creatorId, userId);
        }
        else
        {
            int rows = interactionMapper.insertFollow(creatorId, userId);
            if (favoriteMapper.countByUserAndTarget(userId, "creator", creatorId) == 0)
            {
                CreativeFavorite favorite = new CreativeFavorite();
                favorite.setUserId(userId);
                favorite.setTargetType("creator");
                favorite.setTargetId(creatorId);
                favorite.setStatus("0");
                favoriteMapper.insertCreativeFavorite(favorite);
            }
            return rows;
        }
    }

    @Override
    public boolean isFollowed(Long creatorId, Long userId)
    {
        return userId != null && interactionMapper.checkIsFollowed(creatorId, userId) > 0;
    }

    @Override
    public int insertShare(String targetType, Long targetId, Long userId, String platform)
    {
        return interactionMapper.insertShare(targetType, targetId, userId, platform);
    }
}
