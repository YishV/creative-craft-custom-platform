package com.ruoyi.system.service.creative.support;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeDataPermissionService
{
    private static final String CREATOR_STATUS_NORMAL = "0";
    private static final String CREATOR_AUDIT_APPROVED = "approved";

    @Autowired
    private CreativeCreatorMapper creativeCreatorMapper;

    public Long getCurrentUserId()
    {
        return SecurityUtils.getUserId();
    }

    public boolean isAdmin()
    {
        return SecurityUtils.isAdmin();
    }

    public Long getCurrentCreatorIdOrNull()
    {
        if (isAdmin())
        {
            return null;
        }
        CreativeCreator query = new CreativeCreator();
        query.setUserId(getCurrentUserId());
        List<CreativeCreator> creators = creativeCreatorMapper.selectCreativeCreatorList(query);
        if (creators == null)
        {
            return null;
        }
        return creators.stream()
            .filter(this::isEffectiveCreator)
            .findFirst()
            .map(CreativeCreator::getCreatorId)
            .orElse(null);
    }

    public Long requireCurrentCreatorId()
    {
        Long creatorId = getCurrentCreatorIdOrNull();
        if (creatorId == null)
        {
            throw new ServiceException("当前用户未绑定有效创作者身份");
        }
        return creatorId;
    }

    private boolean isEffectiveCreator(CreativeCreator creator)
    {
        return creator != null
            && CREATOR_STATUS_NORMAL.equals(creator.getStatus())
            && CREATOR_AUDIT_APPROVED.equals(creator.getAuditStatus());
    }
}
