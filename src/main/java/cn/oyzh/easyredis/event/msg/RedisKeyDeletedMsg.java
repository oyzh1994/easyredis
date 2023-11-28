package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyDeletedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_KEY_DELETED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisDBTreeItem item;

    @Setter
    private String key;

    @Override
    public String formatMsg() {
        return String.format(
                "[%s] 删除键[%s-db%s]",
                this.item.info().getName(), this.key, this.item.dbIndex()
        );
    }
}
