package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class RedisConnectionClosedEvent extends Event<RedisClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().infoName());
    }

    public RedisInfo info() {
        return this.data().redisInfo();
    }
}
