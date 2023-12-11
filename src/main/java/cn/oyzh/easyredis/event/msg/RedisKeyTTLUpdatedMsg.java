package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/11
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyTTLUpdatedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_KEY_TTL_UPDATED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisKeyTreeItem<?, ?> item;

    @Setter
    private Long ttl;

    @Override
    public String formatMsg() {
        return String.format(
                "[%s] 键TTL更新[%s-db%s] ttl:%s",
                this.item.info().getName(), item.key(), this.item.dbIndex(), this.ttl
        );
    }
}
