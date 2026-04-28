package com.ruoyi.common.core.domain.event;

import org.springframework.context.ApplicationEvent;

/**
 * 聊天推送事件
 * 
 * @author ruoyi
 */
public class ChatPushEvent extends ApplicationEvent
{
    private Long receiverId;
    private String message;

    public ChatPushEvent(Object source, Long receiverId, String message)
    {
        super(source);
        this.receiverId = receiverId;
        this.message = message;
    }

    public Long getReceiverId()
    {
        return receiverId;
    }

    public String getMessage()
    {
        return message;
    }
}
