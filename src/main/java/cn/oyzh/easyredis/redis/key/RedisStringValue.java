package cn.oyzh.easyredis.redis.key;

import cn.oyzh.easyredis.util.RedisCacheUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisStringValue implements RedisKeyValue<Object> {

    /**
     * 统计值
     */
    @Getter
    @Setter
    private Long count;

    /**
     * 统计值标志位
     */
    @Setter
    @Getter
    private Boolean hyLog;

    public RedisStringValue() {
    }

    public RedisStringValue(String value) {
        this.setValue(value);
    }

    public RedisStringValue(byte[] value) {
        this.setValue(value);
    }

    public static RedisStringValue valueOf(String value) {
        return new RedisStringValue(value);
    }

    public static RedisStringValue valueOf(byte[] value) {
        return new RedisStringValue(value);
    }

    @Override
    public void setValue(Object value) {
        RedisCacheUtil.cacheValue(this.hashCode(), value, (byte) 0);
    }

    @Override
    public Object getValue() {
        return RedisCacheUtil.loadValue(this.hashCode(), (byte) 0);
    }

    public boolean hasValue() {
        return RedisCacheUtil.hasValue(this.hashCode(), (byte) 0);
    }

    @Override
    public Object getUnSavedValue() {
        return RedisCacheUtil.loadValue(this.hashCode(), (byte) 1);
    }

    @Override
    public void setUnSavedValue(Object unSavedValue) {
        RedisCacheUtil.cacheValue(this.hashCode(), unSavedValue, (byte) 1);
    }
}
