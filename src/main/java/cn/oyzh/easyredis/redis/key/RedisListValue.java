package cn.oyzh.easyredis.redis.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
            int index = 0;
            for (String element : elements) {
                rows.add(new RedisListRow(index++, element));
            }
        }
        return new RedisListValue(rows);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisListRow implements RedisKeyRow {

        private int index;

        private String value;
    }


}
