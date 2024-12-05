package cn.oyzh.easyredis.redis.key;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author oyzh
 * @since 2024-12-02
 */
public interface RedisKeyRow {

    int getIndex();

    void setIndex(int index);

    String getValue();

    void setValue(String value);
}
