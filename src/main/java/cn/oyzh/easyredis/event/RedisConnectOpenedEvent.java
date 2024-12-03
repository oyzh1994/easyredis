package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class RedisConnectOpenedEvent extends Event<RedisConnectTreeItem>  {

    public RedisClient client() {
        return this.data().client();
    }

    public RedisConnect redisConnect() {
        return this.data().client().redisInfo();
    }
}
