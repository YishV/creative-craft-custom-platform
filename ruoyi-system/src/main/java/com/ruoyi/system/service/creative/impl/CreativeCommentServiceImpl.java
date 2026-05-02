package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.mapper.creative.CreativeCommentMapper;
import com.ruoyi.system.service.creative.ICreativeCommentService;
import com.ruoyi.system.service.creative.ISensitiveWordService;

@Service
public class CreativeCommentServiceImpl implements ICreativeCommentService
{
    private static final String AUDIT_PENDING = "pending";
    private static final String AUDIT_APPROVED = "approved";
    private static final String AUDIT_REJECTED = "rejected";

    @Autowired
    private CreativeCommentMapper creativeCommentMapper;

    @Autowired
    private ISensitiveWordService sensitiveWordService;

    @Override
    public CreativeComment selectCreativeCommentByCommentId(Long commentId)
    {
        return creativeCommentMapper.selectCreativeCommentByCommentId(commentId);
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.CREATOR, field = "ownerCreatorId")
    public List<CreativeComment> selectCreativeCommentList(CreativeComment creativeComment)
    {
        return creativeCommentMapper.selectCreativeCommentList(creativeComment);
    }

    @Override
    public int insertCreativeComment(CreativeComment creativeComment)
    {
        sensitiveWordService.enforceClean(creativeComment.getCommentContent(), "评论内容");
        if (StringUtils.isBlank(creativeComment.getAuditStatus()))
        {
            creativeComment.setAuditStatus(AUDIT_PENDING);
        }
        return creativeCommentMapper.insertCreativeComment(creativeComment);
    }

    @Override
    public int updateCreativeComment(CreativeComment creativeComment)
    {
        return creativeCommentMapper.updateCreativeComment(creativeComment);
    }

    @Override
    public int deleteCreativeCommentByCommentId(Long commentId)
    {
        return creativeCommentMapper.deleteCreativeCommentByCommentId(commentId);
    }

    @Override
    public int deleteCreativeCommentByCommentIds(Long[] commentIds)
    {
        return creativeCommentMapper.deleteCreativeCommentByCommentIds(commentIds);
    }

    @Override
    public int approveAudit(Long commentId, String operator)
    {
        CreativeComment existing = creativeCommentMapper.selectCreativeCommentByCommentId(commentId);
        if (existing == null)
        {
            throw new ServiceException("评论不存在: " + commentId);
        }
        if (AUDIT_APPROVED.equals(existing.getAuditStatus()))
        {
            throw new ServiceException("评论已审核通过");
        }
        CreativeComment update = new CreativeComment();
        update.setCommentId(commentId);
        update.setAuditStatus(AUDIT_APPROVED);
        update.setUpdateBy(operator);
        return creativeCommentMapper.updateCreativeComment(update);
    }

    @Override
    public int rejectAudit(Long commentId, String remark, String operator)
    {
        if (StringUtils.isBlank(remark))
        {
            throw new ServiceException("驳回必须填写理由");
        }
        CreativeComment existing = creativeCommentMapper.selectCreativeCommentByCommentId(commentId);
        if (existing == null)
        {
            throw new ServiceException("评论不存在: " + commentId);
        }
        if (AUDIT_REJECTED.equals(existing.getAuditStatus()))
        {
            throw new ServiceException("评论已被驳回");
        }
        CreativeComment update = new CreativeComment();
        update.setCommentId(commentId);
        update.setAuditStatus(AUDIT_REJECTED);
        update.setRemark(remark);
        update.setUpdateBy(operator);
        return creativeCommentMapper.updateCreativeComment(update);
    }
}
