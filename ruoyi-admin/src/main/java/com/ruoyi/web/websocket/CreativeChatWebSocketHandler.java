package com.ruoyi.web.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.service.creative.ICreativeChatService;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CreativeChatWebSocketHandler extends TextWebSocketHandler
{
    private static final Logger log = LoggerFactory.getLogger(CreativeChatWebSocketHandler.class);

    private static final String ATTR_USER_ID = "userId";
    private static final String ATTR_USERNAME = "username";

    private final Map<Long, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ICreativeChatService creativeChatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String token = getQueryValue(session.getUri(), "token");
        LoginUser loginUser = tokenService.getLoginUser(token);
        if (loginUser == null || loginUser.getUserId() == null)
        {
            sendError(session, "登录状态已失效，请重新登录");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            return;
        }

        session.getAttributes().put(ATTR_USER_ID, loginUser.getUserId());
        session.getAttributes().put(ATTR_USERNAME, loginUser.getUsername());
        sessions.computeIfAbsent(loginUser.getUserId(), key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception
    {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        String username = (String) session.getAttributes().get(ATTR_USERNAME);
        if (userId == null)
        {
            sendError(session, "登录状态已失效，请重新登录");
            return;
        }

        try
        {
            CreativeChatMessageRequest request = objectMapper.readValue(textMessage.getPayload(), CreativeChatMessageRequest.class);
            CreativeChatMessage message = creativeChatService.sendMessage(request, userId, username);
            String payload = objectMapper.writeValueAsString(Map.of("type", "message", "data", message));
            sendToUsers(payload, userId, message.getReceiverId());
        }
        catch (ServiceException e)
        {
            sendError(session, e.getMessage());
        }
        catch (JsonProcessingException | IllegalArgumentException e)
        {
            sendError(session, "消息格式错误");
        }
        catch (Exception e)
        {
            log.error("聊天消息处理异常", e);
            sendError(session, "消息发送失败，请稍后重试");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        removeSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception
    {
        removeSession(session);
        if (session.isOpen())
        {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private void sendToUsers(String payload, Long... userIds) throws IOException
    {
        Set<Long> uniqueUserIds = new HashSet<>();
        for (Long userId : userIds)
        {
            if (userId != null && uniqueUserIds.add(userId))
            {
                sendIfOpen(userId, payload);
            }
        }
    }

    private void sendIfOpen(Long userId, String payload) throws IOException
    {
        Set<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions == null || userSessions.isEmpty())
        {
            return;
        }
        for (WebSocketSession target : userSessions)
        {
            if (target.isOpen())
            {
                synchronized (target)
                {
                    if (target.isOpen())
                    {
                        target.sendMessage(new TextMessage(payload));
                    }
                }
            }
            else
            {
                userSessions.remove(target);
            }
        }
        if (userSessions.isEmpty())
        {
            sessions.remove(userId, userSessions);
        }
    }

    private void sendError(WebSocketSession session, String message) throws IOException
    {
        if (session != null && session.isOpen())
        {
            String payload = objectMapper.writeValueAsString(Map.of("type", "error", "message", message));
            synchronized (session)
            {
                if (session.isOpen())
                {
                    session.sendMessage(new TextMessage(payload));
                }
            }
        }
    }

    private void removeSession(WebSocketSession session)
    {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (userId == null)
        {
            return;
        }
        Set<WebSocketSession> userSessions = sessions.get(userId);
        if (userSessions != null)
        {
            userSessions.remove(session);
            if (userSessions.isEmpty())
            {
                sessions.remove(userId, userSessions);
            }
        }
    }

    private String getQueryValue(URI uri, String key)
    {
        if (uri == null || StringUtils.isEmpty(uri.getQuery()))
        {
            return null;
        }
        String prefix = key + "=";
        for (String part : uri.getQuery().split("&"))
        {
            if (part.startsWith(prefix))
            {
                return URLDecoder.decode(part.substring(prefix.length()), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
