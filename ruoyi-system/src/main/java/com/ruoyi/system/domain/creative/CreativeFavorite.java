package com.ruoyi.system.domain.creative;

import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeFavorite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long favoriteId;
    private Long userId;
    private String userName;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String status;

    public Long getFavoriteId()
    {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId)
    {
        this.favoriteId = favoriteId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

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

    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
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
