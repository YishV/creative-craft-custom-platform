package com.ruoyi.system.service.creative.support;

import com.ruoyi.common.annotation.CreativeDataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import java.util.List;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeDataPermissionService
{
    private static final String CREATOR_STATUS_NORMAL = "0";
    private static final String CREATOR_AUDIT_APPROVED = "approved";
    private static final String NO_PERMISSION_MESSAGE = "无权操作该数据";

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

    /**
     * 切面入口：按声明的 owner 把当前用户的归属字段注入查询参数。
     *
     * @return true 继续执行底层方法；false 表示当前角色无对应身份，调用方应返回空列表
     */
    public boolean applyListScope(Object query, CreativeDataScope.Owner owner, String field)
    {
        if (isAdmin())
        {
            return true;
        }
        if (query == null)
        {
            return true;
        }
        switch (owner)
        {
            case BUYER:
                writeField(query, field, getCurrentUserId());
                return true;
            case CREATOR:
                Long creatorId = getCurrentCreatorIdOrNull();
                if (creatorId == null)
                {
                    return false;
                }
                writeField(query, field, creatorId);
                return true;
            default:
                return true;
        }
    }

    public void ensureBuyerOwned(Long ownerUserId)
    {
        if (isAdmin())
        {
            return;
        }
        if (ownerUserId == null || !getCurrentUserId().equals(ownerUserId))
        {
            throw new ServiceException(NO_PERMISSION_MESSAGE);
        }
    }

    public void ensureCreatorOwned(Long ownerCreatorId)
    {
        if (isAdmin())
        {
            return;
        }
        Long currentCreatorId = requireCurrentCreatorId();
        if (ownerCreatorId == null || !currentCreatorId.equals(ownerCreatorId))
        {
            throw new ServiceException(NO_PERMISSION_MESSAGE);
        }
    }

    public void ensureOrderOwned(Long buyerId, Long sellerId)
    {
        if (isAdmin())
        {
            return;
        }
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(buyerId))
        {
            return;
        }
        Long currentCreatorId = getCurrentCreatorIdOrNull();
        if (currentCreatorId != null && currentCreatorId.equals(sellerId))
        {
            return;
        }
        throw new ServiceException(NO_PERMISSION_MESSAGE);
    }

    private void writeField(Object target, String field, Object value)
    {
        BeanWrapper wrapper = new BeanWrapperImpl(target);
        if (!wrapper.isWritableProperty(field))
        {
            throw new ServiceException("字段 " + field + " 不可写，无法注入数据权限范围");
        }
        wrapper.setPropertyValue(field, value);
    }

    private boolean isEffectiveCreator(CreativeCreator creator)
    {
        return creator != null
            && CREATOR_STATUS_NORMAL.equals(creator.getStatus())
            && CREATOR_AUDIT_APPROVED.equals(creator.getAuditStatus());
    }
}
