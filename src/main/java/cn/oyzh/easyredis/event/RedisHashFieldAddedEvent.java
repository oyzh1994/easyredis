package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
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
public class RedisHashFieldAddedEvent extends Event<RedisHashKeyTreeItem> implements EventFormatter {

    private String key;

    private String field;

    private String value;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.addField() + ":%s " + I18nHelper.value() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.field, this.value
        );
    }
}
