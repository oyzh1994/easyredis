package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisHashFieldAddedEvent extends Event<RedisHashKeyTreeItem> implements EventFormatter {

    @Setter
    private String key;

    @Setter
    private String field;

    @Setter
    private String value;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.addField() + ":%s " + I18nHelper.value() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.field, this.value
        );
    }
}
