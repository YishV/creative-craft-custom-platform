package com.ruoyi.system.domain.creative;

import java.io.Serializable;

public class CreativeChatSessionRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String targetType;
    private Long targetId;
    private Long creatorId;

    public String getTargetType()
    {
        return targetType;
    }

    public void setTargetType(String targetType)
    {
        this.targetType = targetType;
    }

    public Long getTargetId()
    {
        return targetId;
    }

    public void setTargetId(Long targetId)
    {
        this.targetId = targetId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }
}
