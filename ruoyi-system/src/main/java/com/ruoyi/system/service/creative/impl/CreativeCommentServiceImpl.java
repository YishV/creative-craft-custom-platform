package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.creative.CreativeComment;
import com.ruoyi.system.mapper.creative.CreativeCommentMapper;
import com.ruoyi.system.service.creative.ICreativeCommentService;
import com.ruoyi.system.service.creative.ISensitiveWordService;

@Service
public class CreativeCommentServiceImpl implements ICreativeCommentService
{
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
    public List<CreativeComment> selectCreativeCommentList(CreativeComment creativeComment)
    {
        return creativeCommentMapper.selectCreativeCommentList(creativeComment);
    }

    @Override
    public int insertCreativeComment(CreativeComment creativeComment)
    {
        sensitiveWordService.enforceClean(creativeComment.getCommentContent(), "评论内容");
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
}
