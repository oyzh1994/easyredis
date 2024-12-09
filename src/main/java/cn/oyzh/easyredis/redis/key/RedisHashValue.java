package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import lombok.Data;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
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

    public RedisHashValue() {
    }

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
    // public RedisHashValue deserialize(byte[] bytes) {
    //     String str = new String(bytes, StandardCharsets.UTF_8);
    //     JSONObject object = JSONUtil.parseObject(str);
    //     JSONArray value = object.getJSONArray("value");
    //     if (value == null) {
    //         return null;
    //     }
    //     return new RedisHashValue(value.toBeanList(RedisHashValue.RedisHashRow.class));
    // }

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
