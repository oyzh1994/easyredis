package cn.oyzh.easyredis.redis.key;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisSetValue implements RedisKeyValue<List<RedisSetValue.RedisSetRow>> {

    @Getter
    private List<RedisSetRow> value;

    public RedisSetValue(List<RedisSetRow> value) {
        this.value = value;
    }

    public static RedisSetValue valueOf(Set<String> members) {
        List<RedisSetRow> rows = new ArrayList<>();
        if (members != null) {
            for (String member : members) {
                rows.add(new RedisSetRow(member));
            }
        }
        return new RedisSetValue(rows);
    }

    @Data
    public static class RedisSetRow implements RedisKeyRow {

        private byte index;

        private String value;

        public RedisSetRow() {
        }

        public RedisSetRow(String value) {
            this.value = value;
        }

        @Override
        public RedisSetRow clone() {
            return new RedisSetRow(this.value);
        }
    }

}
