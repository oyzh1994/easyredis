package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisSetValue {

    private List<RedisSetRow> value;

    public RedisSetValue(List<RedisSetRow> value) {
        this.value = value;
    }

    public List<RedisSetRow> getValue() {
        return value;
    }

    public class RedisSetRow implements RedisKeyRow {

        @Getter
        private String value;
    }

}
