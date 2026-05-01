package com.ruoyi.system.mapper.creative;

import com.ruoyi.system.domain.creative.SensitiveWord;
import java.util.List;

public interface SensitiveWordMapper
{
    SensitiveWord selectSensitiveWordById(Long wordId);

    List<SensitiveWord> selectSensitiveWordList(SensitiveWord query);

    List<String> selectEnabledWords();

    int insertSensitiveWord(SensitiveWord sensitiveWord);

    int updateSensitiveWord(SensitiveWord sensitiveWord);

    int deleteSensitiveWordByIds(Long[] wordIds);

    SensitiveWord selectByWord(String word);
}
