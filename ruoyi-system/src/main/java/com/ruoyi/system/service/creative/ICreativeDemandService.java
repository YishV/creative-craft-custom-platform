package com.ruoyi.system.service.creative;

import java.util.List;
import com.ruoyi.system.domain.creative.CreativeDemand;

public interface ICreativeDemandService
{
    CreativeDemand selectCreativeDemandByDemandId(Long demandId);

    List<CreativeDemand> selectCreativeDemandList(CreativeDemand creativeDemand);

    int insertCreativeDemand(CreativeDemand creativeDemand);

    int updateCreativeDemand(CreativeDemand creativeDemand);

    int deleteCreativeDemandByDemandId(Long demandId);

    int deleteCreativeDemandByDemandIds(Long[] demandIds);

    /**
     * 推进需求状态，校验状态机合法性。
     */
    int transitDemandStatus(Long demandId, String targetStatus, String operator);
}
