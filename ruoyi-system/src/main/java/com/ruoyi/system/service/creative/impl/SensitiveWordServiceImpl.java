package com.ruoyi.system.service.creative.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.creative.SensitiveCheckResult;
import com.ruoyi.system.domain.creative.SensitiveWord;
import com.ruoyi.system.mapper.creative.SensitiveWordMapper;
import com.ruoyi.system.service.creative.ISensitiveWordService;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 敏感词服务：基于 DFA（确定有限状态自动机）的多模式串匹配。
 *
 * 词库变动后调用 {@link #reload()} 热更新 DFA 树（CRUD 内部已自动调用）。
 */
@Service
public class SensitiveWordServiceImpl implements ISensitiveWordService
{
    private static final Logger log = LoggerFactory.getLogger(SensitiveWordServiceImpl.class);

    /** DFA 子节点 + 终止标记的容器节点 */
    private static class Node
    {
        Map<Character, Node> children = new HashMap<>(2);
        boolean end = false;
    }

    /** 整棵树用 AtomicReference 维护，写时构建新树原子替换，读侧无锁 */
    private final AtomicReference<Node> root = new AtomicReference<>(new Node());

    private static final char MASK_CHAR = '*';

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @PostConstruct
    public void init()
    {
        reload();
    }

    @Override
    public SensitiveWord selectSensitiveWordById(Long wordId)
    {
        return sensitiveWordMapper.selectSensitiveWordById(wordId);
    }

    @Override
    public List<SensitiveWord> selectSensitiveWordList(SensitiveWord query)
    {
        return sensitiveWordMapper.selectSensitiveWordList(query);
    }

    @Override
    public int insertSensitiveWord(SensitiveWord sensitiveWord)
    {
        normalize(sensitiveWord);
        SensitiveWord existed = sensitiveWordMapper.selectByWord(sensitiveWord.getWord());
        if (existed != null)
        {
            throw new ServiceException("敏感词已存在：" + sensitiveWord.getWord());
        }
        int rows = sensitiveWordMapper.insertSensitiveWord(sensitiveWord);
        reload();
        return rows;
    }

    @Override
    public int updateSensitiveWord(SensitiveWord sensitiveWord)
    {
        normalize(sensitiveWord);
        int rows = sensitiveWordMapper.updateSensitiveWord(sensitiveWord);
        reload();
        return rows;
    }

    @Override
    public int deleteSensitiveWordByIds(Long[] wordIds)
    {
        int rows = sensitiveWordMapper.deleteSensitiveWordByIds(wordIds);
        reload();
        return rows;
    }

    @Override
    public SensitiveCheckResult check(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return SensitiveCheckResult.clean(text);
        }
        Node tree = root.get();
        if (tree == null || tree.children.isEmpty())
        {
            return SensitiveCheckResult.clean(text);
        }

        Set<String> hits = new LinkedHashSet<>();
        char[] chars = text.toCharArray();
        char[] masked = chars.clone();

        int i = 0;
        while (i < chars.length)
        {
            int matchedLength = matchFrom(tree, chars, i);
            if (matchedLength > 0)
            {
                hits.add(new String(chars, i, matchedLength));
                for (int j = 0; j < matchedLength; j++)
                {
                    masked[i + j] = MASK_CHAR;
                }
                i += matchedLength;
            }
            else
            {
                i++;
            }
        }

        if (hits.isEmpty())
        {
            return SensitiveCheckResult.clean(text);
        }
        return SensitiveCheckResult.of(true, hits, new String(masked));
    }

    @Override
    public void enforceClean(String text, String fieldName)
    {
        SensitiveCheckResult r = check(text);
        if (r.isHit())
        {
            String label = StringUtils.isNotEmpty(fieldName) ? fieldName : "内容";
            throw new ServiceException(label + "包含敏感词：" + String.join("、", r.getHits()));
        }
    }

    @Override
    public synchronized void reload()
    {
        List<String> words = sensitiveWordMapper.selectEnabledWords();
        Node fresh = buildTree(words);
        root.set(fresh);
        log.info("[SensitiveWord] DFA 重建完成，词库 {} 条", words == null ? 0 : words.size());
    }

    private static Node buildTree(List<String> words)
    {
        Node root = new Node();
        if (words == null)
        {
            return root;
        }
        for (String w : words)
        {
            if (w == null || w.isEmpty())
            {
                continue;
            }
            Node cur = root;
            for (int i = 0; i < w.length(); i++)
            {
                char c = w.charAt(i);
                Node next = cur.children.get(c);
                if (next == null)
                {
                    next = new Node();
                    cur.children.put(c, next);
                }
                cur = next;
            }
            cur.end = true;
        }
        return root;
    }

    /**
     * 从 chars[start] 开始尝试沿 DFA 走，返回最长匹配长度（无匹配返回 0）。
     */
    private static int matchFrom(Node root, char[] chars, int start)
    {
        Node cur = root;
        int matched = 0;
        for (int i = start; i < chars.length; i++)
        {
            Node next = cur.children.get(chars[i]);
            if (next == null)
            {
                break;
            }
            cur = next;
            if (cur.end)
            {
                matched = i - start + 1;
            }
        }
        return matched;
    }

    private static void normalize(SensitiveWord w)
    {
        if (w == null)
        {
            throw new ServiceException("敏感词不能为空");
        }
        if (w.getWord() != null)
        {
            w.setWord(w.getWord().trim());
        }
        if (StringUtils.isEmpty(w.getWord()))
        {
            throw new ServiceException("敏感词不能为空");
        }
        if (w.getWord().length() > 64)
        {
            throw new ServiceException("敏感词长度不能超过 64");
        }
        if (StringUtils.isEmpty(w.getCategory()))
        {
            w.setCategory("general");
        }
        if (StringUtils.isEmpty(w.getSeverity()))
        {
            w.setSeverity("1");
        }
        if (StringUtils.isEmpty(w.getStatus()))
        {
            w.setStatus("0");
        }
    }
}
