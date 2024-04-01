package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyDeletedMsg extends Event<RedisDBTreeItem> implements  EventFormatter {

    @Setter
    private String key;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 删除键[%s-db%s]",
                this.data().info().getName(), this.key, this.data().dbIndex()
        );
    }
}
