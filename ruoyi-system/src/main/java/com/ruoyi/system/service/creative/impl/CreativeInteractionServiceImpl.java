package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.mapper.creative.CreativeInteractionMapper;
import com.ruoyi.system.service.creative.ICreativeInteractionService;

/**
 * 社区交互 Service 业务层处理
 */
@Service
public class CreativeInteractionServiceImpl implements ICreativeInteractionService
{
    @Autowired
    private CreativeInteractionMapper interactionMapper;

    @Override
    public List<CreativeComment> selectCommentList(CreativeComment comment)
    {
        return interactionMapper.selectCommentList(comment);
    }

    @Override
    public int insertComment(CreativeComment comment)
    {
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
            return interactionMapper.deleteFollow(creatorId, userId);
        }
        else
        {
            return interactionMapper.insertFollow(creatorId, userId);
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
