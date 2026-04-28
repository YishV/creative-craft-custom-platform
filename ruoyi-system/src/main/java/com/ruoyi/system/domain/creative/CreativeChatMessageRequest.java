package com.ruoyi.system.domain.creative;

import java.io.Serializable;

public class CreativeChatMessageRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private String messageType;
    private String content;

    public Long getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(Long sessionId)
    {
        this.sessionId = sessionId;
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
}
