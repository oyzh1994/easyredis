package cn.oyzh.easyredis.redis.key;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisHashValue implements RedisKeyValue<List<RedisHashValue.RedisHashRow>> {

    @Getter
    private List<RedisHashRow> value;

    public RedisHashValue(List<RedisHashRow> value) {
        this.value = value;
    }

    public static RedisHashValue valueOf(Map<String, String> value) {
        List<RedisHashRow> rows = new ArrayList<>();
        if (value != null) {
            for (Map.Entry<String, String> entry : value.entrySet()) {
                rows.add(new RedisHashRow(entry.getKey(), entry.getValue()));
            }
        }
        return new RedisHashValue(rows);
    }

    @Data
    public static class RedisHashRow implements RedisKeyRow {

        private byte index;

        private String field;

        private String value;

        public RedisHashRow() {

        }

        public RedisHashRow(String field, String value) {
            this.field = field;
            this.value = value;
        }

        @Override
        public RedisHashRow clone() {
            return new RedisHashRow(this.field, this.value);
        }
    }
}
