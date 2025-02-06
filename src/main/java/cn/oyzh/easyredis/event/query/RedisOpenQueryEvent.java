package cn.oyzh.easyredis.event.query;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author oyzh
 * @since 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RedisOpenQueryEvent extends Event<RedisQuery> {

    private RedisClient client;

    public RedisConnect redisConnect() {
        return this.client.redisConnect();
    }
}
