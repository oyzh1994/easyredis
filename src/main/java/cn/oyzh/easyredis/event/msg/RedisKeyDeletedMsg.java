package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyDeletedMsg implements EventMsg {

    private final String name = RedisEventTypes.REDIS_KEY_DELETED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisDBTreeItem item;

}
