package cn.oyzh.easyredis.event.query;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024-11-18
 */
public class RedisOpenQueryEvent extends Event<RedisQuery> {

    private RedisClient client;

    public RedisClient getClient() {
        return client;
    }

    public void setClient(RedisClient client) {
        this.client = client;
    }

    public RedisConnect redisConnect() {
        return this.client.redisConnect();
    }
}
