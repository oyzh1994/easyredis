package cn.oyzh.easyredis.redis.key;

import cn.oyzh.easyredis.util.RedisCacheUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-02
 */
public class RedisListValue implements RedisKeyValue<List<RedisListValue.RedisListRow>> {

    @Getter
    @Setter
    private List<RedisListRow> value;

    @Getter
    private RedisListValue.RedisListRow unSavedRow;

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
        if (unSavedValue instanceof RedisListRow) {
            this.unSavedRow = (RedisListRow) unSavedValue;
        }
    }

    public static class RedisListRow implements RedisKeyRow {

        @Getter
        @Setter
        private byte index;

        public RedisListRow(String value) {
            this.setValue(value);
        }

        @Override
        public void setValue(String value) {
            RedisCacheUtil.cacheValue(this.hashCode(), value,"value");
        }

        @Override
        public String getValue() {
            return (String) RedisCacheUtil.loadValue(this.hashCode(), "value");
        }

        @Override
        public RedisListRow clone() {
            return new RedisListRow(this.getValue());
        }
    }
}
