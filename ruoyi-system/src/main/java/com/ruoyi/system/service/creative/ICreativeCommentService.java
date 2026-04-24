package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeComment;

public interface ICreativeCommentService
{
    CreativeComment selectCreativeCommentByCommentId(Long commentId);

    List<CreativeComment> selectCreativeCommentList(CreativeComment creativeComment);

    int insertCreativeComment(CreativeComment creativeComment);

    int updateCreativeComment(CreativeComment creativeComment);

    int deleteCreativeCommentByCommentId(Long commentId);

    int deleteCreativeCommentByCommentIds(Long[] commentIds);
}
