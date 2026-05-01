package com.ruoyi.system.service.creative.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
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
import com.ruoyi.system.service.creative.ISensitiveWordService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CreativeChatServiceImplTest
{
    @Mock
    private CreativeChatSessionMapper sessionMapper;

    @Mock
    private CreativeChatMessageMapper messageMapper;

    @Mock
    private CreativeProductMapper productMapper;

    @Mock
    private CreativeCreatorMapper creatorMapper;

    @Mock
    private CreativeDemandMapper demandMapper;

    @Mock
    private CreativeOrderMapper orderMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ISensitiveWordService sensitiveWordService;

    @InjectMocks
    private CreativeChatServiceImpl chatService;

    @Test
    void openSessionShouldCreateProductSession()
    {
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(buildProduct("0"));
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(buildCreator("0", "approved"));

        CreativeChatSession result = chatService.openSession(buildSessionRequest(), 5L, "buyer");

        ArgumentCaptor<CreativeChatSession> captor = ArgumentCaptor.forClass(CreativeChatSession.class);
        verify(sessionMapper).insertCreativeChatSession(captor.capture());
        assertSame(captor.getValue(), result);
        assertEquals(5L, result.getBuyerId());
        assertEquals(3L, result.getCreatorId());
        assertEquals(8L, result.getCreatorUserId());
        assertEquals("product", result.getTargetType());
        assertEquals(10L, result.getTargetId());
        assertEquals("手作杯垫", result.getTargetName());
        assertEquals("0", result.getStatus());
        assertEquals(0, result.getBuyerUnread());
        assertEquals(0, result.getCreatorUnread());
        assertEquals(8L, result.getPeerUserId());
        assertEquals("木作匠人", result.getPeerName());
        assertEquals(0, result.getUnreadCount());
    }

    @Test
    void openSessionShouldRejectOffShelfProduct()
    {
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(buildProduct("1"));

        assertThrows(ServiceException.class, () -> chatService.openSession(buildSessionRequest(), 5L, "buyer"));

        verify(sessionMapper, never()).insertCreativeChatSession(any(CreativeChatSession.class));
    }

    @Test
    void openSessionShouldRejectInvalidCreator()
    {
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(buildProduct("0"));
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(buildCreator("0", "pending"));

        assertThrows(ServiceException.class, () -> chatService.openSession(buildSessionRequest(), 5L, "buyer"));

        verify(sessionMapper, never()).insertCreativeChatSession(any(CreativeChatSession.class));
    }

    @Test
    void openSessionShouldRejectSelfChat()
    {
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(buildProduct("0"));
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(buildCreator("0", "approved"));

        assertThrows(ServiceException.class, () -> chatService.openSession(buildSessionRequest(), 8L, "creator"));

        verify(sessionMapper, never()).insertCreativeChatSession(any(CreativeChatSession.class));
    }

    @Test
    void openSessionShouldReuseExistingSession()
    {
        CreativeChatSession existing = buildSession();
        existing.setBuyerUnread(2);
        when(productMapper.selectCreativeProductByProductId(10L)).thenReturn(buildProduct("0"));
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(buildCreator("0", "approved"));
        when(sessionMapper.selectExistingSession(5L, 8L, "product", 10L)).thenReturn(existing);

        CreativeChatSession result = chatService.openSession(buildSessionRequest(), 5L, "buyer");

        assertSame(existing, result);
        assertEquals(8L, result.getPeerUserId());
        assertEquals("木作匠人", result.getPeerName());
        assertEquals(2, result.getUnreadCount());
        verify(sessionMapper, never()).insertCreativeChatSession(any(CreativeChatSession.class));
    }

    @Test
    void listMySessionsShouldFillPeerAndUnreadForBuyerAndCreator()
    {
        CreativeChatSession buyerView = buildSession();
        buyerView.setBuyerUnread(3);
        buyerView.setCreatorUnread(4);
        CreativeChatSession creatorView = buildSession();
        creatorView.setBuyerId(9L);
        creatorView.setCreatorUserId(5L);
        creatorView.setCreatorId(6L);
        creatorView.setBuyerUnread(7);
        creatorView.setCreatorUnread(8);
        when(sessionMapper.selectSessionListByUserId(5L)).thenReturn(Arrays.asList(buyerView, creatorView));
        when(creatorMapper.selectCreativeCreatorByCreatorId(3L)).thenReturn(buildCreator("0", "approved"));

        List<CreativeChatSession> sessions = chatService.listMySessions(5L);

        assertEquals(2, sessions.size());
        assertEquals(8L, sessions.get(0).getPeerUserId());
        assertEquals("木作匠人", sessions.get(0).getPeerName());
        assertEquals(3, sessions.get(0).getUnreadCount());
        assertEquals(9L, sessions.get(1).getPeerUserId());
        assertEquals("买家 #9", sessions.get(1).getPeerName());
        assertEquals(8, sessions.get(1).getUnreadCount());
    }

    @Test
    void listMessagesShouldRequireParticipantAndMarkRead()
    {
        CreativeChatSession session = buildSession();
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(session);
        when(messageMapper.selectMessagesBySessionId(1L)).thenReturn(Arrays.asList(buildMessage("text", "你好")));

        List<CreativeChatMessage> messages = chatService.listMessages(1L, 8L);

        assertEquals(1, messages.size());
        verify(messageMapper).markSessionMessagesRead(1L, 8L);
        verify(sessionMapper).clearUnread(1L, "creator_unread");
    }

    @Test
    void listMessagesShouldRejectNonParticipant()
    {
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(buildSession());

        assertThrows(ServiceException.class, () -> chatService.listMessages(1L, 99L));

        verify(messageMapper, never()).selectMessagesBySessionId(1L);
        verify(messageMapper, never()).markSessionMessagesRead(1L, 99L);
    }

    @Test
    void sendMessageShouldPersistTextAndIncrementReceiverUnread()
    {
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(buildSession());

        CreativeChatMessage message = chatService.sendMessage(buildMessageRequest("text", "可以定制颜色吗"), 5L, "buyer");

        ArgumentCaptor<CreativeChatMessage> messageCaptor = ArgumentCaptor.forClass(CreativeChatMessage.class);
        ArgumentCaptor<CreativeChatSession> sessionCaptor = ArgumentCaptor.forClass(CreativeChatSession.class);
        verify(messageMapper).insertCreativeChatMessage(messageCaptor.capture());
        verify(sessionMapper).updateCreativeChatSession(sessionCaptor.capture());
        verify(sessionMapper).incrementUnread(1L, "creator_unread");
        verify(sensitiveWordService).enforceClean("可以定制颜色吗", "聊天内容");
        assertSame(messageCaptor.getValue(), message);
        assertEquals(5L, message.getSenderId());
        assertEquals(8L, message.getReceiverId());
        assertEquals("text", message.getMessageType());
        assertEquals("可以定制颜色吗", message.getContent());
        assertEquals("0", message.getReadStatus());
        assertEquals("可以定制颜色吗", sessionCaptor.getValue().getLastMessage());
        assertNotNull(sessionCaptor.getValue().getLastMessageTime());
    }

    @Test
    void sendMessageShouldPersistImageSummaryAndIncrementBuyerUnread()
    {
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(buildSession());

        chatService.sendMessage(buildMessageRequest("image", " /profile/upload/a.png "), 8L, "creator");

        ArgumentCaptor<CreativeChatMessage> messageCaptor = ArgumentCaptor.forClass(CreativeChatMessage.class);
        ArgumentCaptor<CreativeChatSession> sessionCaptor = ArgumentCaptor.forClass(CreativeChatSession.class);
        verify(messageMapper).insertCreativeChatMessage(messageCaptor.capture());
        verify(sessionMapper).updateCreativeChatSession(sessionCaptor.capture());
        verify(sessionMapper).incrementUnread(1L, "buyer_unread");
        verify(sensitiveWordService, never()).enforceClean(any(String.class), any(String.class));
        assertEquals(5L, messageCaptor.getValue().getReceiverId());
        assertEquals("/profile/upload/a.png", messageCaptor.getValue().getContent());
        assertEquals("[图片]", sessionCaptor.getValue().getLastMessage());
    }

    @Test
    void sendMessageShouldRejectNonParticipant()
    {
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(buildSession());

        assertThrows(ServiceException.class, () -> chatService.sendMessage(buildMessageRequest("text", "你好"), 99L, "stranger"));

        verify(messageMapper, never()).insertCreativeChatMessage(any(CreativeChatMessage.class));
        verify(sessionMapper, never()).incrementUnread(any(Long.class), any(String.class));
    }

    @Test
    void sendMessageShouldRejectIllegalType()
    {
        assertThrows(ServiceException.class, () -> chatService.sendMessage(buildMessageRequest("file", "x"), 5L, "buyer"));

        verify(sessionMapper, never()).selectCreativeChatSessionBySessionId(any(Long.class));
        verify(messageMapper, never()).insertCreativeChatMessage(any(CreativeChatMessage.class));
    }

    @Test
    void markReadShouldClearCurrentUserUnreadAndMessages()
    {
        when(sessionMapper.selectCreativeChatSessionBySessionId(1L)).thenReturn(buildSession());
        when(sessionMapper.clearUnread(1L, "buyer_unread")).thenReturn(1);

        int rows = chatService.markRead(1L, 5L);

        assertEquals(1, rows);
        verify(messageMapper).markSessionMessagesRead(1L, 5L);
        verify(sessionMapper).clearUnread(1L, "buyer_unread");
    }

    private CreativeChatSessionRequest buildSessionRequest()
    {
        CreativeChatSessionRequest request = new CreativeChatSessionRequest();
        request.setTargetType("product");
        request.setTargetId(10L);
        return request;
    }

    private CreativeChatMessageRequest buildMessageRequest(String type, String content)
    {
        CreativeChatMessageRequest request = new CreativeChatMessageRequest();
        request.setSessionId(1L);
        request.setMessageType(type);
        request.setContent(content);
        return request;
    }

    private CreativeChatSession buildSession()
    {
        CreativeChatSession session = new CreativeChatSession();
        session.setSessionId(1L);
        session.setBuyerId(5L);
        session.setCreatorId(3L);
        session.setCreatorUserId(8L);
        session.setTargetType("product");
        session.setTargetId(10L);
        session.setTargetName("手作杯垫");
        session.setBuyerUnread(0);
        session.setCreatorUnread(0);
        return session;
    }

    private CreativeChatMessage buildMessage(String type, String content)
    {
        CreativeChatMessage message = new CreativeChatMessage();
        message.setSessionId(1L);
        message.setSenderId(5L);
        message.setReceiverId(8L);
        message.setMessageType(type);
        message.setContent(content);
        return message;
    }

    private CreativeProduct buildProduct(String status)
    {
        CreativeProduct product = new CreativeProduct();
        product.setProductId(10L);
        product.setProductName("手作杯垫");
        product.setCreatorId(3L);
        product.setPrice(BigDecimal.TEN);
        product.setStatus(status);
        return product;
    }

    private CreativeCreator buildCreator(String status, String auditStatus)
    {
        CreativeCreator creator = new CreativeCreator();
        creator.setCreatorId(3L);
        creator.setUserId(8L);
        creator.setCreatorName("木作匠人");
        creator.setStatus(status);
        creator.setAuditStatus(auditStatus);
        return creator;
    }
}
