package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class RedisFilterMainMsg implements EventMsg {

    private final String name = RedisEventTypes.REDIS_FILTER_MAIN;

    private final String group = RedisEventGroups.FILTER_ACTION;
}
