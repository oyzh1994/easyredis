package cn.oyzh.easyredis.redis;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * redis键，支持行数据
 *
 * @author oyzh
 * @since 2023/6/16
 */
public abstract class RedisRowKey<R extends RedisRow> extends RedisKey {

    /**
     * 行数据
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    protected List<R> value;

}
