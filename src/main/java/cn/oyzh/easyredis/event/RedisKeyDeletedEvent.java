package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.keys.RedisDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyDeletedEvent extends Event<RedisDatabaseTreeItem> implements  EventFormatter {

    private String key;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] [%s-db%s]"+ I18nHelper.keyDeleted() ,
                this.data().info().getName(), this.key, this.data().dbIndex()
        );
    }
}
