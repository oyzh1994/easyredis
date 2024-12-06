package cn.oyzh.easyredis.redis.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
            int index = 0;
            for (Map.Entry<String, String> entry : value.entrySet()) {
                rows.add(new RedisHashRow(index++, entry.getKey(), entry.getValue()));
            }
        }
        return new RedisHashValue(rows);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisHashRow implements RedisKeyRow {

        private int index;

        private String field;

        private String value;

        @Override
        public RedisHashRow clone() {
            RedisHashRow row = new RedisHashRow();
            row.index = this.index;
            row.field = this.field;
            row.value = this.value;
            return row;
        }
    }
}
