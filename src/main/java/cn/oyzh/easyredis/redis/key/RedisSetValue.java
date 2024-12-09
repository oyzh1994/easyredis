package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.util.RedisCacheUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisSetValue implements RedisKeyValue<List<RedisSetValue.RedisSetRow>> {

    @Getter
    @Setter
    private List<RedisSetRow> value;

    @Setter
    @Getter
    private RedisSetRow unSavedRow;

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
        if (unSavedValue instanceof RedisSetRow) {
            this.unSavedRow = (RedisSetRow) unSavedValue;
        }
    }

    public static class RedisSetRow implements RedisKeyRow {

        @Getter
        @Setter
        private byte index;

        public RedisSetRow(String value) {
            this.setValue(value);
        }

        @Override
        public void setValue(String value) {
            // JulLog.info("setValue {}={}", this.hashCode(), value);
            RedisCacheUtil.cacheValue(this.hashCode(), value, (byte) 0);
        }

        @Override
        public String getValue() {
            String value = (String) RedisCacheUtil.loadValue(this.hashCode(), (byte) 0);
            // JulLog.info("getValue {}={}", this.hashCode(), value);
            return value;
        }

        @Override
        public RedisSetRow clone() {
            return new RedisSetRow(this.getValue());
        }
    }
}
