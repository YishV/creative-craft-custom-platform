package com.ruoyi.framework.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;

/**
 * 聊天 WebSocket 服务器
 * 
 * @author ruoyi
 */
@Component
@ServerEndpoint("/chat/{userId}")
public class ChatServer
{
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    /**
     * 记录当前在线连接数
     */
    private static int onlineCount = 0;

    /**
     * concurrent 线程安全 Map，用来存放每个客户端对应的 ChatServer 对象
     */
    private static ConcurrentHashMap<Long, ChatServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收 userId
     */
    private Long userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId)
    {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId))
        {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        }
        else
        {
            webSocketMap.put(userId, this);
            addOnlineCount();
        }
        log.info("用户连接: {}, 当前在线人数为: {}", userId, getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose()
    {
        if (webSocketMap.containsKey(userId))
        {
            webSocketMap.remove(userId);
            subOnlineCount();
        }
        log.info("用户退出: {}, 当前在线人数为: {}", userId, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
        log.info("收到来自用户 {} 的消息: {}", userId, message);
        // 这里可以根据需要处理客户端发来的消息（如心跳检测）
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error)
    {
        log.error("用户错误: {}, 原因: {}", userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送消息
     */
    public void sendMessage(String message) throws IOException
    {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, Long userId)
    {
        log.info("推送消息到用户 {}, 内容: {}", userId, message);
        if (userId != null && webSocketMap.containsKey(userId))
        {
            try
            {
                webSocketMap.get(userId).sendMessage(message);
            }
            catch (IOException e)
            {
                log.error("用户推送失败: {}, 原因: {}", userId, e.getMessage());
            }
        }
        else
        {
            log.warn("用户 {} 不在线，消息已记录", userId);
        }
    }

    public static synchronized int getOnlineCount()
    {
        return onlineCount;
    }

    public static synchronized void addOnlineCount()
    {
        ChatServer.onlineCount++;
    }

    public static synchronized void subOnlineCount()
    {
        ChatServer.onlineCount--;
    }
}
