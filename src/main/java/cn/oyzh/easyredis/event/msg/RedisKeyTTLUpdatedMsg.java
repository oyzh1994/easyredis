package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/11
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyTTLUpdatedMsg extends Event<RedisKeyTreeItem<?, ?>> implements  EventFormatter {

    @Setter
    private Long ttl;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键TTL更新[%s-db%s] ttl:%s",
                this.data().info().getName(), data().key(), this.data().dbIndex(), this.ttl
        );
    }
}
