package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/28
 */
@Getter
@Accessors(fluent = true)
public class RedisConnectionClosedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_CONNECTION_CLOSED;

    private final String group = RedisEventGroups.CONNECTION_ACTION;

    @Setter
    private RedisClient client;

    @Override
    public String formatMsg() {
        return String.format("[%s] 客户端已断开", this.client.infoName());
    }

    public RedisInfo info() {
        return this.client.redisInfo();
    }
}
