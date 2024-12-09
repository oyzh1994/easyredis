package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import lombok.Data;
import lombok.Getter;
import redis.clients.jedis.GeoCoordinate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisZSetValue implements RedisKeyValue<List<RedisZSetValue.RedisZSetRow>> {

    @Getter
    private List<RedisZSetRow> value;

    public RedisZSetValue() {
    }

    public RedisZSetValue(List<RedisZSetRow> value) {
        this.value = value;
    }

    public static RedisZSetValue valueOf(List<String> members, List<Double> scores) {
        List<RedisZSetRow> rows = new ArrayList<>();
        if (members != null) {
            int index = 0;
            for (String member : members) {
                rows.add(new RedisZSetRow(member, scores.get(index++)));
            }
        }
        return new RedisZSetValue(rows);
    }

    public static RedisZSetValue valueOfCoordinates(List<String> members, List<GeoCoordinate> coordinates) {
        List<RedisZSetRow> rows = new ArrayList<>();
        if (members != null) {
            int index = 0;
            for (String member : members) {
                GeoCoordinate coordinate = coordinates.get(index++);
                rows.add(new RedisZSetRow(member, coordinate.getLatitude(), coordinate.getLongitude()));
            }
        }
        return new RedisZSetValue(rows);
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
    // public RedisZSetValue deserialize(byte[] bytes) {
    //     String str = new String(bytes, StandardCharsets.UTF_8);
    //     JSONObject object = JSONUtil.parseObject(str);
    //     JSONArray value = object.getJSONArray("value");
    //     if (value == null) {
    //         return null;
    //     }
    //     this.value = value.toBeanList(RedisZSetRow.class);
    //     return this;
    // }

    @Data
    public static class RedisZSetRow implements RedisKeyRow {

        private byte index;

        private String value;

        private double score;

        private double latitude;

        private double longitude;

        public RedisZSetRow() {
        }

        public RedisZSetRow(String value, double score) {
            this.value = value;
            this.score = score;
        }

        public RedisZSetRow(String value, double latitude, double longitude) {
            this.value = value;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public RedisZSetRow clone() {
            RedisZSetRow row = new RedisZSetRow();
            row.value = this.value;
            row.score = this.score;
            row.latitude = this.latitude;
            row.longitude = this.longitude;
            return row;
        }
    }
}
