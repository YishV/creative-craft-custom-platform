package com.ruoyi.system.domain.creative;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeChatSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Long buyerId;
    private Long creatorId;
    private Long creatorUserId;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    private Integer buyerUnread;
    private Integer creatorUnread;
    private String status;
    private String peerName;
    private Long peerUserId;
    private Integer unreadCount;

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
    }

    public Long getBuyerId()
    {
        return buyerId;
    }

    public void setBuyerId(Long buyerId)
    {
        this.buyerId = buyerId;
    }

    public Long getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(Long creatorId)
    {
        this.creatorId = creatorId;
    }

    public Long getCreatorUserId()
    {
        return creatorUserId;
    }

    public void setCreatorUserId(Long creatorUserId)
    {
        this.creatorUserId = creatorUserId;
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

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime()
    {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime)
    {
        this.lastMessageTime = lastMessageTime;
    }

    public Integer getBuyerUnread()
    {
        return buyerUnread;
    }

    public void setBuyerUnread(Integer buyerUnread)
    {
        this.buyerUnread = buyerUnread;
    }

    public Integer getCreatorUnread()
    {
        return creatorUnread;
    }

    public void setCreatorUnread(Integer creatorUnread)
    {
        this.creatorUnread = creatorUnread;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPeerName()
    {
        return peerName;
    }

    public void setPeerName(String peerName)
    {
        this.peerName = peerName;
    }

    public Long getPeerUserId()
    {
        return peerUserId;
    }

    public void setPeerUserId(Long peerUserId)
    {
        this.peerUserId = peerUserId;
    }

    public Integer getUnreadCount()
    {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount)
    {
        this.unreadCount = unreadCount;
    }
}
