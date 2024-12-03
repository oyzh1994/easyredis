package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/11
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyTTLUpdatedEvent extends Event<RedisKeyTreeItem<?, ?>> implements  EventFormatter {

    private Long ttl;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] "+ I18nHelper.ttlUpdated() +"[%s-db%s] ttl:%s",
                this.data().info().getName(), data().key(), this.data().dbIndex(), this.ttl
        );
    }
}
