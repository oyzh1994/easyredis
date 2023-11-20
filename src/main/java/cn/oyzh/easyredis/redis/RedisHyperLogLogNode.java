package cn.oyzh.easyredis.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/3
 */
public class RedisHyperLogLogNode extends RedisNode {

    /**
     * 节点值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private String value;

    /**
     * 统计值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private Long count;

}
