package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.ArrayUtil;

import java.util.Arrays;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public interface RedisKeyValue<V> {

    V getValue();

    void setValue(V value);

    Object getUnSavedValue();

    void setUnSavedValue(Object unSavedValue);

    // byte[] serialize();
    //
    // RedisKeyValue<?> deserialize(byte[] bytes);
    //
    // static byte[] serializeOf(RedisKeyValue<?> value) {
    //     byte[] bytes = value.serialize();
    //     if (value instanceof RedisStringValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 0;
    //         bytes = bytes1;
    //     } else if (value instanceof RedisListValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 1;
    //         bytes = bytes1;
    //     } else if (value instanceof RedisSetValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 2;
    //         bytes = bytes1;
    //     } else if (value instanceof RedisZSetValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 3;
    //         bytes = bytes1;
    //     } else if (value instanceof RedisHashValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 4;
    //         bytes = bytes1;
    //     } else if (value instanceof RedisStreamValue) {
    //         byte[] bytes1 = Arrays.copyOf(bytes, bytes.length + 1);
    //         bytes1[bytes.length] = 5;
    //         bytes = bytes1;
    //     }
    //     return bytes;
    // }
    //
    // static RedisKeyValue<?> deserializeOf(byte[] bytes) {
    //     int type = bytes[bytes.length - 1];
    //     byte[] bytes1 =   ArrayUtil.copy(bytes, bytes.length - 1);
    //     if (type == 0) {
    //         return new RedisStringValue().deserialize(bytes1);
    //     }
    //     if (type == 1) {
    //         return new RedisListValue().deserialize(bytes1);
    //     }
    //     if (type == 2) {
    //         return new RedisSetValue().deserialize(bytes1);
    //     }
    //     if (type == 3) {
    //         return new RedisZSetValue().deserialize(bytes1);
    //     }
    //     if (type == 4) {
    //         return new RedisHashValue().deserialize(bytes1);
    //     }
    //     if (type == 5) {
    //         return new RedisStreamValue().deserialize(bytes1);
    //     }
    //     return null;
    // }
}
