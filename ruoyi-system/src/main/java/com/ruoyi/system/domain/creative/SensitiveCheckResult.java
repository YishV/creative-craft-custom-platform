package com.ruoyi.system.domain.creative;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SensitiveCheckResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    private boolean hit;
    private Set<String> hits;
    private String masked;

    public SensitiveCheckResult()
    {
        this.hits = new LinkedHashSet<>();
    }

    public static SensitiveCheckResult clean(String original)
    {
        SensitiveCheckResult r = new SensitiveCheckResult();
        r.setHit(false);
        r.setMasked(original);
        return r;
    }

    public static SensitiveCheckResult of(boolean hit, Set<String> hits, String masked)
    {
        SensitiveCheckResult r = new SensitiveCheckResult();
        r.setHit(hit);
        r.setHits(hits == null ? Collections.emptySet() : hits);
        r.setMasked(masked);
        return r;
    }

    public boolean isHit()
    {
        return hit;
    }

    public void setHit(boolean hit)
    {
        this.hit = hit;
    }

    public Set<String> getHits()
    {
        return hits;
    }

    public void setHits(Set<String> hits)
    {
        this.hits = hits;
    }

    public List<String> getHitList()
    {
        return hits == null ? Collections.emptyList() : new java.util.ArrayList<>(hits);
    }

    public String getMasked()
    {
        return masked;
    }

    public void setMasked(String masked)
    {
        this.masked = masked;
    }
}
