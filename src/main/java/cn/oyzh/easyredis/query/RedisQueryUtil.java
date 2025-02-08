package cn.oyzh.easyredis.query;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2025/01/21
 */
@UtilityClass
public class RedisQueryUtil {

    /**
     * 关键字
     */
    private static final Set<String> KEYWORDS = new HashSet<>();

    /**
     * 参数
     */
    private static final Set<String> PARAMS = new HashSet<>();

    /**
     * 键
     */
    private static final Set<String> KEYS = new HashSet<>();

    static {
        // 关键字
        Collection<TerminalCommandHandler<?, ?>> handlers = TerminalManager.listHandler();
        for (TerminalCommandHandler<?, ?> handler : handlers) {
            if (StringUtil.isNotBlank(handler.commandName())) {
                KEYWORDS.add(handler.commandName());
            }
            if (StringUtil.isNotBlank(handler.commandSubName())) {
                KEYWORDS.add(handler.commandSubName());
            }
        }
        // 参数
        PARAMS.add("WITHSCORES");
    }

    public static Set<String> getKeywords() {
        return KEYWORDS;
    }

    public static Set<String> getParams() {
        return PARAMS;
    }

    public static Set<String> getKeys() {
        return KEYS;
    }

    public static void setKeys(Collection<String> keys) {
        KEYS.clear();
        if (keys != null) {
            KEYS.addAll(keys);
        }
    }

    public static double clacCorr(String str, String text) {
        str = str.toUpperCase();
        text = text.toUpperCase();
        if (!str.contains(text) && !text.contains(str)) {
            return 0.d;
        }
        double corr = StringUtil.similarity(str, text);
        if (str.startsWith(text)) {
            corr += 0.3;
        } else if (str.contains(text)) {
            corr += 0.2;
        } else if (str.endsWith(text)) {
            corr += 0.1;
        }
        return corr;
    }

    /**
     * 初始化提示词
     *
     * @param token   提示词
     * @param minCorr 最低相关度
     * @return 结果
     */
    public static List<RedisQueryPromptItem> initPrompts(RedisQueryToken token, float minCorr) {
        if (token == null) {
            return Collections.emptyList();
        }
        // 当前提示词
        String text = token.getContent().toUpperCase();
        // 提示词列表
        final List<RedisQueryPromptItem> items = new CopyOnWriteArrayList<>();
        // 任务列表
        List<Runnable> tasks = new ArrayList<>();
        // 关键字
        if (token.isPossibilityKeyword()) {
            tasks.add(() -> getKeywords().parallelStream().forEach(keyword -> {
                // 计算相关度
                double corr = clacCorr(keyword, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 1);
                    item.setContent(keyword);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 参数
        if (token.isPossibilityParam()) {
            tasks.add(() -> getParams().parallelStream().forEach(param -> {
                // 计算相关度
                double corr = clacCorr(param, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 2);
                    item.setContent(param);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 键
        if (token.isPossibilityKey()) {
            tasks.add(() -> getKeys().parallelStream().forEach(key -> {
                // 计算相关度
                double corr = clacCorr(key, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 3);
                    item.setContent(key);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 执行任务
        ThreadUtil.submitVirtual(tasks);
        // 根据相关度排序
        return items.parallelStream()
                .sorted(Comparator.comparingDouble(RedisQueryPromptItem::getCorrelation))
                .collect(Collectors.toList())
                .reversed();
    }
}
