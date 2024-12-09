package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import lombok.Data;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisListValue implements RedisKeyValue<List<RedisListValue.RedisListRow>> {

    @Getter
    private List<RedisListRow> value;

    public RedisListValue() {
    }

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

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public void setValue(List<RedisListRow> value) {

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
    // public RedisListValue deserialize(byte[] bytes) {
    //     String str = new String(bytes, StandardCharsets.UTF_8);
    //     JSONObject object = JSONUtil.parseObject(str);
    //     JSONArray value = object.getJSONArray("value");
    //     if (value == null) {
    //         return null;
    //     }
    //     this.value =value.toBeanList(RedisListRow.class);
    //     return this;
    // }

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
