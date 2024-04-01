package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/28
 */
@Getter
@Accessors(fluent = true)
public class RedisConnectionClosedMsg extends Event<RedisClient> implements  EventFormatter {

    private final String name = RedisEventTypes.REDIS_CONNECTION_CLOSED;

    private final String group = RedisEventGroups.CONNECTION_ACTION;

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().infoName());
    }

    public RedisInfo info() {
        return this.data().redisInfo();
    }
}
