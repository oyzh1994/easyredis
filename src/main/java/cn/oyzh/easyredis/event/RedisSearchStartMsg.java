package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.search.RedisSearchParam;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/11
 */
@Getter
@Accessors(fluent = true)
public class RedisSearchStartMsg extends Event<RedisSearchParam> {


}
