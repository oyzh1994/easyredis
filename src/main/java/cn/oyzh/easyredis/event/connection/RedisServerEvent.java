package cn.oyzh.easyredis.event.connection;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/4/1
 */
public class RedisServerEvent extends Event<RedisClient> {

    public RedisConnect redisConnect() {
        return this.data().redisConnect();
    }
}
