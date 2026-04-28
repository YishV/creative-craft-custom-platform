package com.ruoyi.system.domain.creative;

import com.ruoyi.common.core.domain.BaseEntity;

public class CreativeChatMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long sessionId;
    private Long senderId;
    private Long receiverId;
    private String messageType;
    private String content;
    private String readStatus;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
    }

    public Long getSenderId()
    {
        return senderId;
    }

    public void setSenderId(Long senderId)
    {
        this.senderId = senderId;
    }

    public Long getReceiverId()
    {
        return receiverId;
    }

    public void setReceiverId(Long receiverId)
    {
        this.receiverId = receiverId;
    }

    public String getMessageType()
    {
        return messageType;
    }

    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getReadStatus()
    {
        return readStatus;
    }

    public void setReadStatus(String readStatus)
    {
        this.readStatus = readStatus;
    }
}
