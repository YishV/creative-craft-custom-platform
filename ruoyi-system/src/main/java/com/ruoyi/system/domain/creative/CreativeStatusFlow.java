package com.ruoyi.system.domain.creative;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文创业务状态机常量与流转规则
 *
 * 集中维护 demand / quote / order 的合法状态及合法的状态迁移，
 * 便于 Service 层调用 {@link #ensureDemandTransition} 等方法进行校验，
 * 避免业务方法散落 if-else 判断。
 */
public final class CreativeStatusFlow
{
    private CreativeStatusFlow() {}

    public static final class Demand
    {
        public static final String DRAFT = "draft";
        public static final String PUBLISHED = "published";
        public static final String QUOTING = "quoting";
        public static final String SELECTED = "selected";
        public static final String CLOSED = "closed";
    }

    public static final class Quote
    {
        public static final String PENDING = "pending";
        public static final String SELECTED = "selected";
        public static final String REJECTED = "rejected";
    }

    public static final class Order
    {
        public static final String CREATED = "created";
        public static final String MAKING = "making";
        public static final String SHIPPED = "shipped";
        public static final String FINISHED = "finished";
        public static final String CANCELLED = "cancelled";
    }

    private static final Map<String, Set<String>> DEMAND_TRANSITIONS = new HashMap<>();
    private static final Map<String, Set<String>> ORDER_TRANSITIONS = new HashMap<>();

    static
    {
        DEMAND_TRANSITIONS.put(Demand.DRAFT, set(Demand.PUBLISHED, Demand.CLOSED));
        DEMAND_TRANSITIONS.put(Demand.PUBLISHED, set(Demand.QUOTING, Demand.CLOSED));
        DEMAND_TRANSITIONS.put(Demand.QUOTING, set(Demand.SELECTED, Demand.CLOSED));
        DEMAND_TRANSITIONS.put(Demand.SELECTED, set(Demand.CLOSED));
        DEMAND_TRANSITIONS.put(Demand.CLOSED, set());

        ORDER_TRANSITIONS.put(Order.CREATED, set(Order.MAKING, Order.CANCELLED));
        ORDER_TRANSITIONS.put(Order.MAKING, set(Order.SHIPPED, Order.CANCELLED));
        ORDER_TRANSITIONS.put(Order.SHIPPED, set(Order.FINISHED));
        ORDER_TRANSITIONS.put(Order.FINISHED, set());
        ORDER_TRANSITIONS.put(Order.CANCELLED, set());
    }

    public static boolean canDemandTransit(String from, String to)
    {
        return canTransit(DEMAND_TRANSITIONS, from, to);
    }

    public static boolean canOrderTransit(String from, String to)
    {
        return canTransit(ORDER_TRANSITIONS, from, to);
    }

    public static void ensureDemandTransition(String from, String to)
    {
        if (!canDemandTransit(from, to))
        {
            throw new IllegalStateException("需求状态不允许从 [" + from + "] 流转到 [" + to + "]");
        }
    }

    public static void ensureOrderTransition(String from, String to)
    {
        if (!canOrderTransit(from, to))
        {
            throw new IllegalStateException("订单状态不允许从 [" + from + "] 流转到 [" + to + "]");
        }
    }

    private static boolean canTransit(Map<String, Set<String>> table, String from, String to)
    {
        if (from == null || to == null)
        {
            return false;
        }
        Set<String> next = table.get(from);
        return next != null && next.contains(to);
    }

    private static Set<String> set(String... values)
    {
        return new HashSet<>(asList(values));
    }

    private static List<String> asList(String... values)
    {
        return Arrays.asList(values);
    }
}
