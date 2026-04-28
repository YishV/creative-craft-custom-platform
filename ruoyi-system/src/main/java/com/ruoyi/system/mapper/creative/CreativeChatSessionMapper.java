package com.ruoyi.system.mapper.creative;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.creative.CreativeChatSession;

public interface CreativeChatSessionMapper
{
    CreativeChatSession selectCreativeChatSessionBySessionId(Long sessionId);

    CreativeChatSession selectExistingSession(@Param("buyerId") Long buyerId,
            @Param("creatorUserId") Long creatorUserId,
            @Param("targetType") String targetType,
            @Param("targetId") Long targetId);

    List<CreativeChatSession> selectSessionListByUserId(Long userId);

    int insertCreativeChatSession(CreativeChatSession session);

    int updateCreativeChatSession(CreativeChatSession session);

    int incrementUnread(@Param("sessionId") Long sessionId, @Param("fieldName") String fieldName);

    int clearUnread(@Param("sessionId") Long sessionId, @Param("fieldName") String fieldName);
}
