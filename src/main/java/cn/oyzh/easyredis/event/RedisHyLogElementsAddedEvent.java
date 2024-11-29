package cn.oyzh.easyredis.event;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
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
public class RedisHyLogElementsAddedEvent extends Event<RedisStringKeyTreeItem> implements EventFormatter {

    private String key;

    private String[] elements;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.addElement() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), ArrayUtil.toString(this.elements)
        );
    }
}
