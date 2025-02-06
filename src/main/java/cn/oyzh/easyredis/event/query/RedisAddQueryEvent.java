package cn.oyzh.easyredis.event.query;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024-11-18
 */
public class RedisAddQueryEvent extends Event<RedisClient> {

    public RedisConnect redisConnect(){
        return this.data().redisConnect();
    }
}
