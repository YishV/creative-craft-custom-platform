package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCreator;

public interface ICreativeCreatorService
{
    CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId);

    List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator);

    int insertCreativeCreator(CreativeCreator creativeCreator);

    int updateCreativeCreator(CreativeCreator creativeCreator);

    int deleteCreativeCreatorByCreatorId(Long creatorId);

    int deleteCreativeCreatorByCreatorIds(Long[] creatorIds);
}
