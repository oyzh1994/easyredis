package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
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

    public RedisStreamValue() {
    }

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

    // @Override
    // public byte[] serialize() {
    //     JSONObject obj = new JSONObject();
    //     if (this.value != null) {
    //         obj.put("value", this.value);
    //     }
    //     return obj.toJSONBBytes();
    // }
    //
    // @Override
    // public RedisStreamValue deserialize(byte[] bytes) {
    //     String str = new String(bytes, StandardCharsets.UTF_8);
    //     JSONObject object = JSONUtil.parseObject(str);
    //     JSONArray value = object.getJSONArray("value");
    //     if (value == null) {
    //         return null;
    //     }
    //     this.value = value.toBeanList(RedisStreamRow.class);
    //     return this;
    // }

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
