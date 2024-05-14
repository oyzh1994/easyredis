package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
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
public class RedisListRowAddedEvent extends Event<RedisListKeyTreeItem> implements EventFormatter {

    @Setter
    private String key;

    @Setter
    private String member;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.rowAdded() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member
        );
    }
}
