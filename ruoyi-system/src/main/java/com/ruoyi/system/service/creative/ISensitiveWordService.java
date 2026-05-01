package com.ruoyi.system.service.creative;

import com.ruoyi.system.domain.creative.SensitiveCheckResult;
import com.ruoyi.system.domain.creative.SensitiveWord;
import java.util.List;

public interface ISensitiveWordService
{
    SensitiveWord selectSensitiveWordById(Long wordId);

    List<SensitiveWord> selectSensitiveWordList(SensitiveWord query);

    int insertSensitiveWord(SensitiveWord sensitiveWord);

    int updateSensitiveWord(SensitiveWord sensitiveWord);

    int deleteSensitiveWordByIds(Long[] wordIds);

    /** 检测文本，命中后返回详细信息（不抛异常） */
    SensitiveCheckResult check(String text);

    /** 检测文本，命中即抛 ServiceException（用于业务入口拦截） */
    void enforceClean(String text, String fieldName);

    /** 强制重建 DFA 树（CRUD 后自动调用，也可对外手动触发） */
    void reload();
}
