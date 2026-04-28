package com.ruoyi.framework.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.event.ChatPushEvent;

/**
 * 聊天事件监听器
 * 
 * @author ruoyi
 */
@Component
public class ChatEventListener
{
    @EventListener
    public void handleChatPushEvent(ChatPushEvent event)
    {
        ChatServer.sendInfo(event.getMessage(), event.getReceiverId());
    }
}
