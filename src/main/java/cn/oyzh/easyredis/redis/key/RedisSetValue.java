package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.util.RedisCacheUtil;
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

    @Getter
    private RedisSetRow unSavedRow;

    public RedisSetValue() {
    }

    public RedisSetValue(List<RedisSetRow> value) {
        this.value = value;
    }

    public static RedisSetValue valueOf(Set<String> members) {
        List<RedisSetRow> rows = new ArrayList<>(12);
        if (members != null) {
            for (String member : members) {
                rows.add(new RedisSetRow(member));
            }
        }
        return new RedisSetValue(rows);
    }

    @Override
    public boolean hasValue() {
        return CollectionUtil.isNotEmpty(this.value);
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
            RedisCacheUtil.cacheValue(this.hashCode(), value, "value");
        }

        @Override
        public String getValue() {
            return (String) RedisCacheUtil.loadValue(this.hashCode(), "value");
        }

        @Override
        public RedisSetRow clone() {
            return new RedisSetRow(this.getValue());
        }
    }
}
