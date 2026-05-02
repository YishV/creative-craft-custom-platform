package com.ruoyi.system.service.creative.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.system.domain.creative.CreativePost;
import com.ruoyi.system.mapper.creative.CreativePostMapper;
import com.ruoyi.system.service.creative.ICreativePostService;

@Service
public class CreativePostServiceImpl implements ICreativePostService
{
    @Autowired
    private CreativePostMapper creativePostMapper;

    @Override
    public CreativePost selectCreativePostByPostId(Long postId)
    {
        return creativePostMapper.selectCreativePostByPostId(postId);
    }

    @Override
    @CreativeDataScope(owner = CreativeDataScope.Owner.CREATOR, field = "creatorId")
    public List<CreativePost> selectCreativePostList(CreativePost creativePost)
    {
        return creativePostMapper.selectCreativePostList(creativePost);
    }

    @Override
    public int insertCreativePost(CreativePost creativePost)
    {
        return creativePostMapper.insertCreativePost(creativePost);
    }

    @Override
    public int updateCreativePost(CreativePost creativePost)
    {
        return creativePostMapper.updateCreativePost(creativePost);
    }

    @Override
    public int deleteCreativePostByPostId(Long postId)
    {
        return creativePostMapper.deleteCreativePostByPostId(postId);
    }

    @Override
    public int deleteCreativePostByPostIds(Long[] postIds)
    {
        return creativePostMapper.deleteCreativePostByPostIds(postIds);
    }
}
