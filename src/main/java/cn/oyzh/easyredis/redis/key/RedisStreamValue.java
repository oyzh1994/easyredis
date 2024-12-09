package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.CollectionUtil;
import lombok.Data;
import lombok.Getter;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.resps.StreamEntry;

import java.nio.charset.StandardCharsets;
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
            for (StreamEntry entry : value) {
                rows.add(new RedisStreamRow(entry));
            }
        }
        return new RedisStreamValue(rows);
    }

    @Override
    public boolean hasValue() {
        return CollectionUtil.isNotEmpty(this.value);
    }

    @Override
    public void setValue(List<RedisStreamRow> value) {

    }

    @Override
    public Object getUnSavedValue() {
        return null;
    }

    @Override
    public void clearUnSavedValue() {

    }

    @Override
    public boolean hasUnSavedValue() {
        return false;
    }

    @Override
    public void setUnSavedValue(Object unSavedValue) {

    }

    @Data
    public static class RedisStreamRow implements RedisKeyRow {

        private byte index;

        private StreamEntry entry;

        public RedisStreamRow() {

        }

        public RedisStreamRow(StreamEntry entry) {
            this.entry = entry;
        }

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
