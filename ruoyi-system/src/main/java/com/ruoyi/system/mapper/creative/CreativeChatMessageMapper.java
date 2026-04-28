package com.ruoyi.system.mapper.creative;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.creative.CreativeChatMessage;

public interface CreativeChatMessageMapper
{
    CreativeChatMessage selectCreativeChatMessageByMessageId(Long messageId);

    List<CreativeChatMessage> selectMessagesBySessionId(Long sessionId);

    int insertCreativeChatMessage(CreativeChatMessage message);

    int markSessionMessagesRead(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
}
