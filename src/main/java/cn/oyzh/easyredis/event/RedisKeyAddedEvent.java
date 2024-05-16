package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.db.RedisDBTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyAddedEvent extends Event<RedisDBTreeItem> implements EventFormatter {

    private String type;

    private String key;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.addKey() + "[%s-db%s] " + I18nHelper.keyType() + ":[%s] ",
                this.data().info().getName(), this.data().dbIndex(), this.key, this.type
        );
    }
}
