package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/11
 */
@Getter
@Accessors(fluent = true)
public class RedisSearchStartMsg implements EventMsg {

    private final String name = RedisEventTypes.REDIS_SEARCH_START;

    private final String group = RedisEventGroups.SEARCH_ACTION;

    @Setter
    private RedisSearchParam searchParam;

}
