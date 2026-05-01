package com.ruoyi.system.mapper.creative;

import com.ruoyi.system.domain.creative.CreativeDashboardStats;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CreativeDashboardMapper
{
    CreativeDashboardStats.Summary selectSummary(@Param("activeSince") Date activeSince);

    List<CreativeDashboardStats.TrendItem> selectOrderTrend(@Param("since") Date since);

    List<CreativeDashboardStats.CategoryStat> selectHotCategories(@Param("limit") int limit);

    List<CreativeDashboardStats.ActiveCreator> selectActiveCreators(@Param("since") Date since, @Param("limit") int limit);
}
