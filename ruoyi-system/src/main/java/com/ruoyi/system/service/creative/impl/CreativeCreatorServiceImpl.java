package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.mapper.creative.CreativeCreatorMapper;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.creative.ICreativeCreatorService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreativeCreatorServiceImpl implements ICreativeCreatorService
{
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_DISABLED = "1";
    private static final String AUDIT_PENDING = "pending";
    private static final String AUDIT_APPROVED = "approved";
    private static final String AUDIT_REJECTED = "rejected";
    private static final String CREATOR_ROLE_KEY = "creator";

    @Autowired
    private CreativeCreatorMapper creativeCreatorMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId)
    {
        return creativeCreatorMapper.selectCreativeCreatorByCreatorId(creatorId);
    }

    @Override
    public List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator)
    {
        return creativeCreatorMapper.selectCreativeCreatorList(creativeCreator);
    }

    @Override
    @Transactional
    public int applyCreator(CreativeCreator creativeCreator)
    {
        validateApplyRequest(creativeCreator);
        ensureUserExists(creativeCreator.getUserId());
        ensureNoPendingOrApprovedApplication(creativeCreator.getUserId());
        creativeCreator.setStatus(STATUS_DISABLED);
        creativeCreator.setAuditStatus(AUDIT_PENDING);
        creativeCreator.setAuditRemark(null);
        creativeCreator.setAuditBy(null);
        creativeCreator.setAuditTime(null);
        return creativeCreatorMapper.insertCreativeCreator(creativeCreator);
    }

    @Override
    public int insertCreativeCreator(CreativeCreator creativeCreator)
    {
        if (StringUtils.isBlank(creativeCreator.getAuditStatus()))
        {
            creativeCreator.setAuditStatus(AUDIT_APPROVED);
        }
        return creativeCreatorMapper.insertCreativeCreator(creativeCreator);
    }

    @Override
    public int updateCreativeCreator(CreativeCreator creativeCreator)
    {
        return creativeCreatorMapper.updateCreativeCreator(creativeCreator);
    }

    @Override
    @Transactional
    public int approveCreator(Long creatorId, String operator)
    {
        CreativeCreator creator = getPendingCreator(creatorId);
        ensureUserExists(creator.getUserId());
        sysUserService.appendRoleByKeyIfAbsent(creator.getUserId(), CREATOR_ROLE_KEY);

        CreativeCreator update = new CreativeCreator();
        update.setCreatorId(creatorId);
        update.setStatus(STATUS_NORMAL);
        update.setAuditStatus(AUDIT_APPROVED);
        update.setAuditRemark(null);
        update.setAuditBy(operator);
        update.setAuditTime(new Date());
        update.setUpdateBy(operator);
        return creativeCreatorMapper.updateCreativeCreator(update);
    }

    @Override
    @Transactional
    public int rejectCreator(Long creatorId, String auditRemark, String operator)
    {
        getPendingCreator(creatorId);
        if (StringUtils.isBlank(auditRemark))
        {
            throw new ServiceException("驳回原因不能为空");
        }

        CreativeCreator update = new CreativeCreator();
        update.setCreatorId(creatorId);
        update.setStatus(STATUS_DISABLED);
        update.setAuditStatus(AUDIT_REJECTED);
        update.setAuditRemark(auditRemark.trim());
        update.setAuditBy(operator);
        update.setAuditTime(new Date());
        update.setUpdateBy(operator);
        return creativeCreatorMapper.updateCreativeCreator(update);
    }

    @Override
    public int deleteCreativeCreatorByCreatorId(Long creatorId)
    {
        return creativeCreatorMapper.deleteCreativeCreatorByCreatorId(creatorId);
    }

    @Override
    public int deleteCreativeCreatorByCreatorIds(Long[] creatorIds)
    {
        return creativeCreatorMapper.deleteCreativeCreatorByCreatorIds(creatorIds);
    }

    private void validateApplyRequest(CreativeCreator creativeCreator)
    {
        if (creativeCreator == null)
        {
            throw new ServiceException("申请信息不能为空");
        }
        if (creativeCreator.getUserId() == null)
        {
            throw new ServiceException("申请用户不能为空");
        }
        if (StringUtils.isBlank(creativeCreator.getCreatorName()))
        {
            throw new ServiceException("创作者名称不能为空");
        }
        if (StringUtils.isBlank(creativeCreator.getStoreName()))
        {
            throw new ServiceException("店铺名称不能为空");
        }
    }

    private void ensureUserExists(Long userId)
    {
        SysUser user = sysUserService.selectUserById(userId);
        if (user == null)
        {
            throw new ServiceException("申请用户不存在");
        }
    }

    private void ensureNoPendingOrApprovedApplication(Long userId)
    {
        CreativeCreator query = new CreativeCreator();
        query.setUserId(userId);
        List<CreativeCreator> creators = creativeCreatorMapper.selectCreativeCreatorList(query);
        if (creators == null)
        {
            return;
        }
        for (CreativeCreator creator : creators)
        {
            if (AUDIT_PENDING.equals(creator.getAuditStatus()) || AUDIT_APPROVED.equals(creator.getAuditStatus()))
            {
                throw new ServiceException("该用户已存在待审核或已通过的创作者申请");
            }
        }
    }

    private CreativeCreator getPendingCreator(Long creatorId)
    {
        CreativeCreator creator = creativeCreatorMapper.selectCreativeCreatorByCreatorId(creatorId);
        if (creator == null)
        {
            throw new ServiceException("创作者申请不存在");
        }
        if (!AUDIT_PENDING.equals(creator.getAuditStatus()))
        {
            throw new ServiceException("只有待审核申请才允许执行当前操作");
        }
        return creator;
    }
}
