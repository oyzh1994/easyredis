package cn.oyzh.easyredis.redis;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * redis键类型
 *
 * @author oyzh
 * @since 2023/07/01
 */
public enum RedisKeyType {
    STRING("字符串"),
    SET("集合"),
    ZSET("有序集合"),
    LIST("列表"),
    HASH("哈希表"),
    HYPERLOGLOG("统计值"),
    STREAM("流");

    @Getter
    @Accessors(fluent = true, chain = false)
    private final String desc;

    RedisKeyType(String desc) {
        this.desc = desc;
    }

    public static RedisKeyType valueOfType(String type) {
        if (StrUtil.isNotBlank(type)) {
            return switch (type.toLowerCase()) {
                case "string", "bitmap" -> STRING;
                case "set" -> SET;
                case "zset", "geo" -> ZSET;
                case "list" -> LIST;
                case "hash" -> HASH;
                case "hyperloglog" -> HYPERLOGLOG;
                case "stream" -> STREAM;
                default -> null;
            };
        }
        return null;
    }

    /**
     * 跟字符串比较
     *
     * @param type 字符串类型
     * @return 结果
     */
    public boolean equalsString(String type) {
        return StrUtil.equalsIgnoreCase(type, this.name());
    }

    /**
     * 枚举长度
     *
     * @return 长度
     */
    public static int length() {
        return RedisKeyType.values().length;
    }
}
