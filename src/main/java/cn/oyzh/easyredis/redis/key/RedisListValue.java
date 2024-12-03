package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisListValue implements RedisKeyValue<List<RedisListValue.RedisListRow>> {

    @Getter
    private List<RedisListRow> value;

    public RedisListValue(List<RedisListRow> value) {
        this.value = value;
    }

    public class RedisListRow implements RedisKeyRow {

        @Getter
        private int index;

        @Getter
        private String value;
    }
}
