package cn.oyzh.easyredis.redis.key;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
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

    public static RedisListValue valueOf(List<String> elements) {
        List<RedisListRow> rows = new ArrayList<>();
        if (elements != null) {
            for (String element : elements) {
                rows.add(new RedisListRow(element));
            }
        }
        return new RedisListValue(rows);
    }

    @Data
    public static class RedisListRow implements RedisKeyRow {

        private byte index;

        private String value;

        public RedisListRow() {
        }

        public RedisListRow(String value) {
            this.value = value;
        }

        @Override
        public RedisListRow clone() {
            return new RedisListRow(this.value);
        }
    }
}
