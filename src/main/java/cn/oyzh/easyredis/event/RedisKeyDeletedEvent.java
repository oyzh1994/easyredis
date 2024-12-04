package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
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
public class RedisKeyDeletedEvent extends Event<RedisConnect> implements  EventFormatter {

    private String key;

    private int dbIndex;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] [%s-db%s]"+ I18nHelper.keyDeleted() ,
                this.data().getName(), this.key, this.dbIndex
        );
    }
}
