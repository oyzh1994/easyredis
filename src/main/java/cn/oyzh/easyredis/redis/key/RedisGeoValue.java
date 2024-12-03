package cn.oyzh.easyredis.redis.key;

import lombok.Getter;

import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisGeoValue implements RedisKeyValue<List<RedisGeoValue.RedisGeoRow>> {

    @Getter
    private List<RedisGeoRow> value;

    public RedisGeoValue(List<RedisGeoRow> value) {
        this.value = value;
    }

    public class RedisGeoRow implements RedisKeyRow {

        @Getter
        private String value;

        @Getter
        private double latitude;

        @Getter
        private double longitude;
    }
}
