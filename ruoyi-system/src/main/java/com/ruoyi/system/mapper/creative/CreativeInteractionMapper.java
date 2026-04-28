package com.ruoyi.system.mapper.creative;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.creative.CreativeComment;

/**
 * 社区交互 Mapper 接口
 */
public interface CreativeInteractionMapper
{
    // 评论相关
    public List<CreativeComment> selectCommentList(CreativeComment comment);
    public int insertComment(CreativeComment comment);
    public int deleteCommentById(Long commentId);

    // 点赞相关
    public int insertLike(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId);
    public int deleteLike(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId);
    public int checkIsLiked(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId);
    public int countLikes(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    // 关注相关
    public int insertFollow(@Param("creatorId") Long creatorId, @Param("userId") Long userId);
    public int deleteFollow(@Param("creatorId") Long creatorId, @Param("userId") Long userId);
    public int checkIsFollowed(@Param("creatorId") Long creatorId, @Param("userId") Long userId);

    // 分享相关
    public int insertShare(@Param("targetType") String targetType, @Param("targetId") Long targetId, @Param("userId") Long userId, @Param("platform") String platform);
}
