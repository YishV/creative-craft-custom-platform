package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeComment;

/**
 * 社区交互 Service 接口
 */
public interface ICreativeInteractionService
{
    public List<CreativeComment> selectCommentList(CreativeComment comment);
    public int insertComment(CreativeComment comment);
    public int deleteCommentById(Long commentId);

    public int toggleLike(String targetType, Long targetId, Long userId);
    public boolean isLiked(String targetType, Long targetId, Long userId);

    public int toggleFollow(Long creatorId, Long userId);
    public boolean isFollowed(Long creatorId, Long userId);

    public int insertShare(String targetType, Long targetId, Long userId, String platform);
}
