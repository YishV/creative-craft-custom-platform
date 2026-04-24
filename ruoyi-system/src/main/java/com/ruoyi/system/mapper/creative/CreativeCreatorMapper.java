package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeCreator;

public interface CreativeCreatorMapper
{
    CreativeCreator selectCreativeCreatorByCreatorId(Long creatorId);

    List<CreativeCreator> selectCreativeCreatorList(CreativeCreator creativeCreator);

    int insertCreativeCreator(CreativeCreator creativeCreator);

    int updateCreativeCreator(CreativeCreator creativeCreator);

    int deleteCreativeCreatorByCreatorId(Long creatorId);

    int deleteCreativeCreatorByCreatorIds(Long[] creatorIds);
}
