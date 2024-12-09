package cn.oyzh.easyredis.redis.key;

import cn.oyzh.easyredis.util.RedisCacheUtil;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.GeoCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisZSetValue implements RedisKeyValue<List<RedisZSetValue.RedisZSetRow>> {

    @Getter
    @Setter
    private List<RedisZSetRow> value;

    @Getter
    private RedisZSetRow unSavedRow;

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

    @Override
    public boolean hasValue() {
        return this.value != null && !this.value.isEmpty();
    }

    @Override
    public Object getUnSavedValue() {
        return this.unSavedRow;
    }

    @Override
    public void clearUnSavedValue() {
        if (this.unSavedRow != null) {
            this.unSavedRow.setValue(null);
            this.unSavedRow = null;
        }
    }

    @Override
    public boolean hasUnSavedValue() {
        return this.unSavedRow != null && this.unSavedRow.getValue() != null;
    }

    @Override
    public void setUnSavedValue(Object unSavedValue) {
        if (unSavedValue instanceof RedisZSetRow) {
            this.unSavedRow = (RedisZSetRow) unSavedValue;
        }
    }

    public static class RedisZSetRow implements RedisKeyRow {

        @Getter
        @Setter
        private byte index;

        @Getter
        @Setter
        private double score;

        @Getter
        @Setter
        private double latitude;

        @Getter
        @Setter
        private double longitude;

        public RedisZSetRow() {
        }

        public RedisZSetRow(String value, double score) {
            this.setValue(value);
            this.score = score;
        }

        public RedisZSetRow(String value, double latitude, double longitude) {
            this.setValue(value);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void setValue(String value) {
            RedisCacheUtil.cacheValue(this.hashCode(), value, "value");
        }

        @Override
        public String getValue() {
            return (String) RedisCacheUtil.loadValue(this.hashCode(), "value");
        }

        @Override
        public RedisZSetRow clone() {
            RedisZSetRow row = new RedisZSetRow();
            row.score = this.score;
            row.latitude = this.latitude;
            row.longitude = this.longitude;
            row.setValue(this.getValue());
            return row;
        }
    }
}
