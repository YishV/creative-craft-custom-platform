package com.ruoyi.system.domain.creative;

import com.ruoyi.common.core.domain.BaseEntity;

public class CreativePost extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long postId;
    private Long creatorId;
    private String creatorName;
    private String storeName;
    private String postTitle;
    private String postType;
    private String status;

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    public String getCreatorName()
    {
        return creatorName;
    }

    public void setCreatorName(String creatorName)
    {
        this.creatorName = creatorName;
    }

    public String getStoreName()
    {
        return storeName;
    }

    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }

    public String getPostTitle()
    {
        return postTitle;
    }

    public void setPostTitle(String postTitle)
    {
        this.postTitle = postTitle;
    }

    public String getPostType()
    {
        return postType;
    }

    public void setPostType(String postType)
    {
        this.postType = postType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
