package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisZSetValue implements RedisKeyValue<List<RedisZSetValue.RedisZSetRow>> {

    @Getter
    private List<RedisZSetRow> value;

    public RedisZSetValue(List<RedisZSetRow> value) {
        this.value = value;
    }

    public class RedisZSetRow implements RedisKeyRow {

        @Getter
        private double score;

        @Getter
        private String value;
    }
}
