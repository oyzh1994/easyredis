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

    // /**
    //  * 键值
    //  */
    // @Getter
    // private Object value;

    /**
     * 统计值标志位
     */
    @Setter
    @Getter
    private Boolean hyLog;

    public RedisStringValue() {
    }

    public RedisStringValue(String value) {
        // this.value = value;
        this.setValue(value);
    }

    public RedisStringValue(byte[] value) {
        // this.value = value;
        this.setValue(value);
    }

    public static RedisStringValue valueOf(String value) {
        return new RedisStringValue(value);
    }

    public static RedisStringValue valueOf(byte[] value) {
        return new RedisStringValue(value);
    }

    public void setValue(Object value) {
        // this.value = value;
        RedisCacheUtil.cacheValue(this.hashCode(), value);
        System.out.println(value);
    }

    public Object getValue() {
        return RedisCacheUtil.loadValue(this.hashCode());
    }

    public boolean hasValue() {
        return RedisCacheUtil.hasValue(this.hashCode());
    }

    // @Override
    // public byte[] serialize() {
    //     // JSONObject obj = new JSONObject();
    //     // obj.put("value", this.value);
    //     // if (this.count != null) {
    //     //     obj.put("count", this.count);
    //     // }
    //     // if (this.hyLog != null) {
    //     //     obj.put("hyLog", this.hyLog);
    //     // }
    //     // return obj.toJSONString().getBytes(StandardCharsets.UTF_8);
    //     return null;
    // }
    //
    // @Override
    // public RedisStringValue deserialize(byte[] bytes) {
    //     // String str = new String(bytes, StandardCharsets.UTF_8);
    //     // JSONObject object = JSONUtil.parseObject(str);
    //     // Object value = object.get("value");
    //     // if (value != null) {
    //     //     this.value = value;
    //     // }
    //     // if (object.containsKey("count")) {
    //     //     this.count = object.getLong("count");
    //     // }
    //     // if (object.containsKey("hyLog")) {
    //     //     this.hyLog = object.getBoolean("hyLog");
    //     // }
    //     return this;
    // }
}
