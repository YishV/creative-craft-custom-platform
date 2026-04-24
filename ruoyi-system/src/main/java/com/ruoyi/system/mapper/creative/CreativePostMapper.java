package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativePost;

public interface CreativePostMapper
{
    CreativePost selectCreativePostByPostId(Long postId);

    List<CreativePost> selectCreativePostList(CreativePost creativePost);

    int insertCreativePost(CreativePost creativePost);

    int updateCreativePost(CreativePost creativePost);

    int deleteCreativePostByPostId(Long postId);

    int deleteCreativePostByPostIds(Long[] postIds);
}
