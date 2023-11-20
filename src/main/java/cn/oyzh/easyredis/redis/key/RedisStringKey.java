package cn.oyzh.easyredis.redis.key;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis string键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisStringKey extends RedisKey {

    /**
     * 值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private Object value;

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    public Integer size() {
        if (this.value == null) {
            return null;
        }
        if (this.value instanceof String string) {
            return string.getBytes().length;
        }
        if (this.value instanceof byte[] bytes) {
            return bytes.length;
        }
        return null;
    }
}
