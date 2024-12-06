package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisStreamValue implements RedisKeyValue<List<RedisStreamValue.RedisStreamRow>> {

    @Getter
    private List<RedisStreamRow> value;

    public RedisStreamValue(List<RedisStreamRow> value) {
        this.value = value;
    }

    public static RedisStreamValue valueOf(List<StreamEntry> value) {
        List<RedisStreamRow> rows = new ArrayList<>();
        if (value != null) {
            int index = 0;
            for (StreamEntry entry : value) {
                rows.add(new RedisStreamRow(index++, entry));
            }
        }
        return new RedisStreamValue(rows);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisStreamRow implements RedisKeyRow {

        private int index;

        private StreamEntry entry;

        public String getId() {
            return this.entry.getID().toString();
        }

        public String getValue() {
            return JSONUtil.toJson(this.entry.getFields());
        }

        @Override
        public void setValue(String value) {
            throw new UnsupportedOperationException();
        }

        public StreamEntryID getStreamId() {
            return this.entry.getID();
        }
    }
}
