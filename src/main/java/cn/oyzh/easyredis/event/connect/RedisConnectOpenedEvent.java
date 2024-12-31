package cn.oyzh.easyredis.event.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class RedisConnectOpenedEvent extends Event<RedisDatabaseTreeItem>  {

    public RedisClient client() {
        return this.data().client();
    }

    public RedisConnect redisConnect() {
        return this.data().client().redisConnect();
    }
}
