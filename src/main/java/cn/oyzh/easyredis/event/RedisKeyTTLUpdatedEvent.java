package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/11
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyTTLUpdatedEvent extends Event<RedisKeyTreeItem<?, ?>> implements  EventFormatter {

    @Setter
    private Long ttl;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] "+ I18nHelper.ttlUpdated() +"[%s-db%s] ttl:%s",
                this.data().info().getName(), data().key(), this.data().dbIndex(), this.ttl
        );
    }
}
