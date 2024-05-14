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
public class RedisKeyRenamedEvent extends Event<RedisKeyTreeItem<?, ?>> implements EventFormatter {

    @Setter
    private String oldKey;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.keyRenamed() + "[%s-db%s] " + I18nHelper.newName() + ":%s",
                this.data().info().getName(), this.oldKey, this.data().dbIndex(), this.data().key()
        );
    }
}
