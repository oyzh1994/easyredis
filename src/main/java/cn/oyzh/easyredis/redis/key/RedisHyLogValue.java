package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisHyLogValue implements RedisKeyValue<Object> {

    @Getter
    private Long count;

    @Getter
    private Object value;

    public RedisHyLogValue(Object value, Long count) {
        this.value = value;
        this.count = count;
    }
}
