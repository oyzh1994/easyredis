package cn.oyzh.easyredis.redis.key;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public interface RedisKeyRow extends Cloneable {

    byte getIndex();

    void setIndex(byte index);

    String getValue();

    void setValue(String value);
}
