package com.ruoyi.system.service.creative;

import com.ruoyi.system.domain.creative.CreativeChatMessage;
import com.ruoyi.system.domain.creative.CreativeChatMessageRequest;
import com.ruoyi.system.domain.creative.CreativeChatSession;
import com.ruoyi.system.domain.creative.CreativeChatSessionRequest;
import java.util.List;

public interface ICreativeChatService
{
    List<CreativeChatSession> listMySessions(Long userId);

    CreativeChatSession openSession(CreativeChatSessionRequest request, Long currentUserId, String username);

    List<CreativeChatMessage> listMessages(Long sessionId, Long currentUserId);

    CreativeChatMessage sendMessage(CreativeChatMessageRequest request, Long currentUserId, String username);

    int markRead(Long sessionId, Long currentUserId);
}
