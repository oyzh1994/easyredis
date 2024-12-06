package cn.oyzh.easyredis.redis.key;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisStringValue implements RedisKeyValue<Object> {

    @Getter
    @Setter
    private Long count;

    /**
     * 统计值
     */
    @Getter
    private Object value;

    /**
     * 统计值标志位
     */
    @Setter
    @Getter
    private Boolean hyLog;

    public RedisStringValue(Object value) {
        this.value = value;
    }

    public static RedisStringValue valueOf(Object value) {
        return new RedisStringValue(value);
    }
}
