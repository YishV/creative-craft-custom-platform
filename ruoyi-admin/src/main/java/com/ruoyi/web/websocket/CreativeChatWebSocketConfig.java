package com.ruoyi.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class CreativeChatWebSocketConfig implements WebSocketConfigurer
{
    @Autowired
    private CreativeChatWebSocketHandler creativeChatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(creativeChatWebSocketHandler, "/ws/chat").setAllowedOrigins("*");
    }
}
