package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisStringValue implements RedisKeyValue<Object> {

    @Getter
    private Object value;

    public RedisStringValue(Object value) {
        this.value = value;
    }
}
