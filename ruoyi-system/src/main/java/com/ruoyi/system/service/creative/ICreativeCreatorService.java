package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCreator;
import com.ruoyi.system.domain.creative.CreativeCreatorProfile;

public interface ICreativeCreatorService
{
    CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId);

    List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator);

    /**
     * 当前登录用户的创作者主页：返回最佳档案（approved > pending > rejected）+ 生效身份下的关键统计。
     * 未提交申请时返回 profile.creator == null，由前端引导申请。
     */
    CreativeCreatorProfile selectMyCreatorProfile();

    int applyCreator(CreativeCreator creativeCreator);

    int insertCreativeCreator(CreativeCreator creativeCreator);

    int updateCreativeCreator(CreativeCreator creativeCreator);

    int approveCreator(Long creatorId, String operator);

    int rejectCreator(Long creatorId, String auditRemark, String operator);

    int deleteCreativeCreatorByCreatorId(Long creatorId);

    int deleteCreativeCreatorByCreatorIds(Long[] creatorIds);
}
