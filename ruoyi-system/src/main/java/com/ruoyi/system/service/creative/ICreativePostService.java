package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativePost;

public interface ICreativePostService
{
    CreativePost selectCreativePostByPostId(Long postId);

    List<CreativePost> selectCreativePostList(CreativePost creativePost);

    int insertCreativePost(CreativePost creativePost);

    int updateCreativePost(CreativePost creativePost);

    int deleteCreativePostByPostId(Long postId);

    int deleteCreativePostByPostIds(Long[] postIds);
}
