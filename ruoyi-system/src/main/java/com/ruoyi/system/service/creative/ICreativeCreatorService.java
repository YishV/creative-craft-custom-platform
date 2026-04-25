package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCreator;

public interface ICreativeCreatorService
{
    CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId);

    List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator);

    int applyCreator(CreativeCreator creativeCreator);

    int insertCreativeCreator(CreativeCreator creativeCreator);

    int updateCreativeCreator(CreativeCreator creativeCreator);

    int approveCreator(Long creatorId, String operator);

    int rejectCreator(Long creatorId, String auditRemark, String operator);

    int deleteCreativeCreatorByCreatorId(Long creatorId);

    int deleteCreativeCreatorByCreatorIds(Long[] creatorIds);
}
