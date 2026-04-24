package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeComment;

public interface CreativeCommentMapper
{
    CreativeComment selectCreativeCommentByCommentId(Long commentId);

    List<CreativeComment> selectCreativeCommentList(CreativeComment creativeComment);

    int insertCreativeComment(CreativeComment creativeComment);

    int updateCreativeComment(CreativeComment creativeComment);

    int deleteCreativeCommentByCommentId(Long commentId);

    int deleteCreativeCommentByCommentIds(Long[] commentIds);
}
