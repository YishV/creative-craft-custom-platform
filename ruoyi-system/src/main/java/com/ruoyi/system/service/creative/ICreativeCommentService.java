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

    /** 审核通过：pending -> approved */
    int approveAudit(Long commentId, String operator);

    /** 审核驳回：pending/approved -> rejected（前台不再展示） */
    int rejectAudit(Long commentId, String remark, String operator);
}
