package com.ruoyi.system.mapper.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeDemand;

public interface CreativeDemandMapper
{
    CreativeDemand selectCreativeDemandByDemandId(Long demandId);

    List<CreativeDemand> selectCreativeDemandList(CreativeDemand creativeDemand);

    int insertCreativeDemand(CreativeDemand creativeDemand);

    int updateCreativeDemand(CreativeDemand creativeDemand);

    int deleteCreativeDemandByDemandId(Long demandId);

    int deleteCreativeDemandByDemandIds(Long[] demandIds);
}
