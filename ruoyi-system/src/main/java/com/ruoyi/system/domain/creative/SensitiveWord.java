package com.ruoyi.system.domain.creative;

import com.ruoyi.common.core.domain.BaseEntity;

public class SensitiveWord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long wordId;

    private String word;

    /** 分类：general/politics/abuse/ad */
    private String category;

    /** 等级：1低 2中 3高 */
    private String severity;

    /** 状态：0启用 1停用 */
    private String status;

    public Long getWordId()
    {
        return wordId;
    }

    public void setWordId(Long wordId)
    {
        this.wordId = wordId;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(String severity)
    {
        this.severity = severity;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
