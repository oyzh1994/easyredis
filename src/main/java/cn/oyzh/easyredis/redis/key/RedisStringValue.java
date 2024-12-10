package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.BooleanUtil;
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
        RedisCacheUtil.cacheValue(this.hashCode(), value, "value");
    }

    @Override
    public Object getValue() {
        return RedisCacheUtil.loadValue(this.hashCode(), "value");
    }

    @Override
    public boolean hasValue() {
        return RedisCacheUtil.hasValue(this.hashCode(), "value");
    }

    @Override
    public Object getUnSavedValue() {
        return RedisCacheUtil.loadValue(this.hashCode(), "unsaved");
    }

    @Override
    public void clearUnSavedValue() {
        RedisCacheUtil.deleteValue(this.hashCode(), "unsaved");
    }

    @Override
    public boolean hasUnSavedValue() {
        return RedisCacheUtil.hasValue(this.hashCode(), "unsaved");
    }

    @Override
    public void setUnSavedValue(Object unSavedValue) {
        RedisCacheUtil.cacheValue(this.hashCode(), unSavedValue, "unsaved");
    }

    public boolean isHyLog() {
        return this.count != null || BooleanUtil.isTrue(this.hyLog);
    }

    public String stringValue() {
        Object value = this.getValue();
        if (value instanceof String s) {
            return s;
        }
        if (value instanceof byte[] bytes) {
            return new String(bytes);
        }
        return "";
    }

    public byte[] bytesValue() {
        Object value = this.getValue();
        if (value instanceof String s) {
            return s.getBytes();
        }
        if (value instanceof byte[] bytes) {
            return bytes;
        }
        return new byte[0];
    }
}
