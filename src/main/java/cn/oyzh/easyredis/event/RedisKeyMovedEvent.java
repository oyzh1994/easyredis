package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/12
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyMovedEvent extends Event<RedisKeyTreeItem> implements EventFormatter {

    private int targetDB;

    public int sourceDB() {
        return this.data().dbIndex();
    }

    public RedisConnect redisConnect() {
        return this.data().redisConnect();
    }

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.keyMoved() + "[%s-db%s] " + I18nHelper.targetDatabase() + ":%s",
                this.redisConnect().getName(), this.data().key(), this.data().dbIndex(), this.targetDB
        );
    }
}
