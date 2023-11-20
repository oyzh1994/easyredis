package cn.oyzh.easyredis.util;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * redis节点工具类
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Slf4j
@UtilityClass
public class RedisNodeUtil {

    /**
     * 是否被过滤
     *
     * @param key 键名称
     * @param filters  过滤配置列表
     * @return 结果
     */
    public static boolean isFiltered(String key, List<RedisFilter> filters) {
        if (CollUtil.isEmpty(filters) || key == null) {
            return false;
        }
        // 匹配结果
        for (RedisFilter filter : filters) {
            // 未启用，不处理
            if (!filter.isEnable()) {
                continue;
            }
            // 模糊匹配
            if (filter.isPartMatch() && key.contains(filter.getKw())) {
                return true;
            }
            // 完全匹配
            if (key.equalsIgnoreCase(filter.getKw())) {
                return true;
            }
        }
        return false;
    }

}
