package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/27
 */
@Getter
@Accessors(fluent = true)
public class TreeChildChangedMsg implements EventMsg {

    private final String name = RedisEventTypes.TREE_CHILD_CHANGED;

    private final String group = RedisEventGroups.TREE_ACTION;
}
