package cn.oyzh.easyredis.redis;

import cn.hutool.core.util.StrUtil;

/**
 * redis键类型
 *
 * @author oyzh
 * @since 2023/07/01
 */
public enum RedisKeyType {
    STRING,
    SET,
    ZSET,
    LIST,
    HASH,
    HYPERLOGLOG,
    STREAM;

    public static RedisKeyType valueOfType(String type) {
        if (StrUtil.isNotBlank(type)) {
            return switch (type.toLowerCase()) {
                case "string" -> STRING;
                case "set" -> SET;
                case "zset" -> ZSET;
                case "list" -> LIST;
                case "hash" -> HASH;
                case "hyperloglog" -> HYPERLOGLOG;
                case "stream" -> STREAM;
                default -> null;
            };
        }
        return null;
    }

    public boolean equalsString(String type) {
        return StrUtil.equalsIgnoreCase(type, this.name());
    }
}
