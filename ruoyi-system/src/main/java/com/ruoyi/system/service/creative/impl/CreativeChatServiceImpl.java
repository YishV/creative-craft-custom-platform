package com.ruoyi.system.service.creative.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.event.ChatPushEvent;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeProduct;
import com.ruoyi.system.mapper.creative.CreativeChatMessageMapper;
import com.ruoyi.system.mapper.creative.CreativeChatSessionMapper;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.mapper.creative.CreativeDemandMapper;
import com.ruoyi.system.mapper.creative.CreativeOrderMapper;
import com.ruoyi.system.mapper.creative.CreativeProductMapper;
import com.ruoyi.system.service.creative.ICreativeChatService;
import com.ruoyi.system.service.creative.ISensitiveWordService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CreativeChatServiceImpl implements ICreativeChatService
{
    private static final String STATUS_NORMAL = "0";
    private static final String AUDIT_APPROVED = "approved";
    private static final String TYPE_PRODUCT = "product";
    private static final String MESSAGE_TEXT = "text";
    private static final String MESSAGE_IMAGE = "image";
    private static final String READ_NO = "0";
    private static final String BUYER_UNREAD = "buyer_unread";
    private static final String CREATOR_UNREAD = "creator_unread";

    @Autowired
    private CreativeChatSessionMapper sessionMapper;

    @Autowired
    private CreativeChatMessageMapper messageMapper;

    @Autowired
    private CreativeProductMapper productMapper;

    @Autowired
    private CreativeCreatorMapper creatorMapper;

    @Autowired
    private CreativeDemandMapper demandMapper;

    @Autowired
    private CreativeOrderMapper orderMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ISensitiveWordService sensitiveWordService;

    @Override
    public List<CreativeChatSession> listMySessions(Long userId)
    {
        List<CreativeChatSession> sessions = sessionMapper.selectSessionListByUserId(userId);
        for (CreativeChatSession session : sessions)
        {
            fillSessionView(session, userId);
        }
        return sessions;
    }

    @Override
    public CreativeChatSession openSession(CreativeChatSessionRequest request, Long currentUserId, String username)
    {
        validateSessionRequest(request);
        if (TYPE_PRODUCT.equals(request.getTargetType()))
        {
            return openProductSession(request.getTargetId(), currentUserId, username);
        }
        throw new ServiceException("暂不支持该聊天入口");
    }

    @Override
    public List<CreativeChatMessage> listMessages(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = requireParticipant(sessionId, currentUserId);
        markRead(session.getSessionId(), currentUserId);
        return messageMapper.selectMessagesBySessionId(sessionId);
    }

    @Override
    public CreativeChatMessage sendMessage(CreativeChatMessageRequest request, Long currentUserId, String username)
    {
        validateMessageRequest(request);
        CreativeChatSession session = requireParticipant(request.getSessionId(), currentUserId);
        Long receiverId = currentUserId.equals(session.getBuyerId()) ? session.getCreatorUserId() : session.getBuyerId();
        String content = request.getContent().trim();

        // 文本消息走敏感词过滤；图片消息内容是 URL，不参与过滤
        if (MESSAGE_TEXT.equals(request.getMessageType()))
        {
            sensitiveWordService.enforceClean(content, "聊天内容");
        }

        CreativeChatMessage message = new CreativeChatMessage();
        message.setSessionId(session.getSessionId());
        message.setSenderId(currentUserId);
        message.setReceiverId(receiverId);
        message.setMessageType(request.getMessageType());
        message.setContent(content);
        message.setReadStatus(READ_NO);
        message.setCreateBy(username);
        messageMapper.insertCreativeChatMessage(message);

        CreativeChatSession update = new CreativeChatSession();
        update.setSessionId(session.getSessionId());
        update.setLastMessage(MESSAGE_IMAGE.equals(request.getMessageType()) ? "[图片]" : abbreviate(content));
        update.setLastMessageTime(new Date());
        update.setUpdateBy(username);
        sessionMapper.updateCreativeChatSession(update);
        sessionMapper.incrementUnread(session.getSessionId(), receiverId.equals(session.getBuyerId()) ? BUYER_UNREAD : CREATOR_UNREAD);

        // 发送实时推送事件
        eventPublisher.publishEvent(new ChatPushEvent(this, receiverId, JSON.toJSONString(message)));

        return message;
    }

    @Override
    public int markRead(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = requireParticipant(sessionId, currentUserId);
        String unreadField = currentUserId.equals(session.getBuyerId()) ? BUYER_UNREAD : CREATOR_UNREAD;
        messageMapper.markSessionMessagesRead(sessionId, currentUserId);
        return sessionMapper.clearUnread(sessionId, unreadField);
    }

    private CreativeChatSession openProductSession(Long productId, Long currentUserId, String username)
    {
        CreativeProduct product = productMapper.selectCreativeProductByProductId(productId);
        if (product == null || !STATUS_NORMAL.equals(product.getStatus()))
        {
            throw new ServiceException("商品不存在或已下架");
        }
        CreativeCreator creator = creatorMapper.selectCreativeCreatorByCreatorId(product.getCreatorId());
        if (creator == null || creator.getUserId() == null || !STATUS_NORMAL.equals(creator.getStatus())
                || !AUDIT_APPROVED.equals(creator.getAuditStatus()))
        {
            throw new ServiceException("创作者不存在或未通过审核");
        }
        if (creator.getUserId().equals(currentUserId))
        {
            throw new ServiceException("不能和自己发起聊天");
        }
        CreativeChatSession existing = sessionMapper.selectExistingSession(currentUserId, creator.getUserId(), TYPE_PRODUCT, productId);
        if (existing != null)
        {
            fillSessionView(existing, currentUserId, creator.getCreatorName());
            return existing;
        }

        CreativeChatSession session = new CreativeChatSession();
        session.setBuyerId(currentUserId);
        session.setCreatorId(creator.getCreatorId());
        session.setCreatorUserId(creator.getUserId());
        session.setTargetType(TYPE_PRODUCT);
        session.setTargetId(productId);
        session.setTargetName(product.getProductName());
        session.setBuyerUnread(0);
        session.setCreatorUnread(0);
        session.setStatus(STATUS_NORMAL);
        session.setCreateBy(username);
        sessionMapper.insertCreativeChatSession(session);
        fillSessionView(session, currentUserId, creator.getCreatorName());
        return session;
    }

    private CreativeChatSession requireParticipant(Long sessionId, Long currentUserId)
    {
        CreativeChatSession session = sessionMapper.selectCreativeChatSessionBySessionId(sessionId);
        if (session == null || currentUserId == null
                || (!currentUserId.equals(session.getBuyerId()) && !currentUserId.equals(session.getCreatorUserId())))
        {
            throw new ServiceException("无权访问该会话");
        }
        return session;
    }

    private void validateSessionRequest(CreativeChatSessionRequest request)
    {
        if (request == null || StringUtils.isEmpty(request.getTargetType()) || request.getTargetId() == null)
        {
            throw new ServiceException("聊天入口参数不完整");
        }
    }

    private void validateMessageRequest(CreativeChatMessageRequest request)
    {
        if (request == null || request.getSessionId() == null || StringUtils.isEmpty(request.getMessageType())
                || StringUtils.isEmpty(request.getContent()) || StringUtils.isEmpty(request.getContent().trim()))
        {
            throw new ServiceException("消息内容不能为空");
        }
        if (!MESSAGE_TEXT.equals(request.getMessageType()) && !MESSAGE_IMAGE.equals(request.getMessageType()))
        {
            throw new ServiceException("不支持的消息类型");
        }
        if (request.getContent().trim().length() > 1000)
        {
            throw new ServiceException("消息内容不能超过1000字");
        }
    }

    private String abbreviate(String text)
    {
        return text.length() > 80 ? text.substring(0, 80) : text;
    }

    private void fillSessionView(CreativeChatSession session, Long currentUserId)
    {
        String creatorName = null;
        if (session.getBuyerId() != null && session.getBuyerId().equals(currentUserId) && session.getCreatorId() != null)
        {
            CreativeCreator creator = creatorMapper.selectCreativeCreatorByCreatorId(session.getCreatorId());
            if (creator != null)
            {
                creatorName = creator.getCreatorName();
            }
        }
        fillSessionView(session, currentUserId, creatorName);
    }

    private void fillSessionView(CreativeChatSession session, Long currentUserId, String creatorName)
    {
        if (session.getBuyerId() != null && session.getBuyerId().equals(currentUserId))
        {
            session.setPeerUserId(session.getCreatorUserId());
            session.setPeerName(StringUtils.isNotEmpty(creatorName) ? creatorName : "创作者 #" + session.getCreatorId());
            session.setUnreadCount(defaultUnread(session.getBuyerUnread()));
        }
        else
        {
            session.setPeerUserId(session.getBuyerId());
            session.setPeerName("买家 #" + session.getBuyerId());
            session.setUnreadCount(defaultUnread(session.getCreatorUnread()));
        }
    }

    private Integer defaultUnread(Integer unread)
    {
        return unread == null ? 0 : unread;
    }
}
