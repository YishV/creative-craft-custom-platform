package com.ruoyi.system.service.creative.impl;

import com.ruoyi.system.domain.creative.CreativeDashboardStats;
import com.ruoyi.system.mapper.creative.CreativeDashboardMapper;
import com.ruoyi.system.service.creative.ICreativeDashboardService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreativeDashboardServiceImpl implements ICreativeDashboardService
{
    private static final int TREND_DAYS = 7;
    private static final int ACTIVE_WINDOW_DAYS = 30;
    private static final int TOP_LIMIT = 5;
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CreativeDashboardMapper creativeDashboardMapper;

    @Override
    public CreativeDashboardStats getStats()
    {
        Date activeSince = daysAgo(ACTIVE_WINDOW_DAYS);
        Date trendSince = daysAgoStartOfDay(TREND_DAYS - 1);

        CreativeDashboardStats stats = new CreativeDashboardStats();
        CreativeDashboardStats.Summary summary = creativeDashboardMapper.selectSummary(activeSince);
        if (summary == null)
        {
            summary = new CreativeDashboardStats.Summary();
            summary.setGmv(BigDecimal.ZERO);
        }
        if (summary.getGmv() == null)
        {
            summary.setGmv(BigDecimal.ZERO);
        }
        stats.setSummary(summary);

        stats.setTrend(buildTrend(creativeDashboardMapper.selectOrderTrend(trendSince)));
        stats.setHotCategories(creativeDashboardMapper.selectHotCategories(TOP_LIMIT));
        stats.setActiveCreators(creativeDashboardMapper.selectActiveCreators(activeSince, TOP_LIMIT));
        return stats;
    }

    private List<CreativeDashboardStats.TrendItem> buildTrend(List<CreativeDashboardStats.TrendItem> rows)
    {
        Map<String, CreativeDashboardStats.TrendItem> indexed = new HashMap<>();
        if (rows != null)
        {
            for (CreativeDashboardStats.TrendItem item : rows)
            {
                indexed.put(item.getDate(), item);
            }
        }
        List<CreativeDashboardStats.TrendItem> filled = new ArrayList<>(TREND_DAYS);
        LocalDate today = LocalDate.now();
        for (int i = TREND_DAYS - 1; i >= 0; i--)
        {
            String day = today.minusDays(i).format(DAY_FORMAT);
            CreativeDashboardStats.TrendItem hit = indexed.get(day);
            if (hit != null)
            {
                if (hit.getGmv() == null)
                {
                    hit.setGmv(BigDecimal.ZERO);
                }
                filled.add(hit);
            }
            else
            {
                filled.add(new CreativeDashboardStats.TrendItem(day, 0L, BigDecimal.ZERO));
            }
        }
        return filled;
    }

    private Date daysAgo(int days)
    {
        LocalDate target = LocalDate.now().minusDays(days);
        return Date.from(target.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date daysAgoStartOfDay(int days)
    {
        return daysAgo(days);
    }
}
