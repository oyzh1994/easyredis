package cn.oyzh.easyredis.event;

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
public class RedisKeyRenamedMsg extends Event<RedisKeyTreeItem<?, ?>> implements  EventFormatter {

    @Setter
    private String oldKey;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键更名[%s-db%s] 新名称:%s",
                this.data().info().getName(), this.oldKey, this.data().dbIndex(), this.data().key()
        );
    }
}
