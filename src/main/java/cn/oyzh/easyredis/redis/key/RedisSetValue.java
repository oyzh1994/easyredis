package cn.oyzh.easyredis.redis.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
            int index = 0;
            for (String member : members) {
                rows.add(new RedisSetRow(index++, member));
            }
        }
        return new RedisSetValue(rows);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSetRow implements RedisKeyRow {

        private int index;

        private String value;

        @Override
        public RedisSetRow clone() {
            RedisSetRow row = new RedisSetRow();
            row.index = this.index;
            row.value = this.value;
            return row;
        }
    }

}
