package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import lombok.Data;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
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

    public RedisSetValue() {
    }

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
    // public RedisSetValue deserialize(byte[] bytes) {
    //     String str = new String(bytes, StandardCharsets.UTF_8);
    //     JSONObject object = JSONUtil.parseObject(str);
    //     JSONArray value = object.getJSONArray("value");
    //     if (value == null) {
    //         return null;
    //     }
    //     this.value = value.toBeanList(RedisSetRow.class);
    //     return this;
    // }

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
