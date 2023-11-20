package cn.oyzh.easyredis.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/3
 */
public class RedisHyperLogLogKey extends RedisKey {

    /**
     * 键值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private byte[] value;

    /**
     * 统计值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private Long count;

    /**
     * 获取数据大小
     *
     * @return 数据大小
     */
    public Integer size() {
        if (this.value == null) {
            return null;
        }
        return this.value.length;
    }
}
