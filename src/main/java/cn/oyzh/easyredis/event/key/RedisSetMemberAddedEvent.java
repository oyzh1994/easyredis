package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.trees.key.RedisSetKeyTreeItem;
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
public class RedisSetMemberAddedEvent extends Event<RedisSetKeyTreeItem> implements EventFormatter {

    private String key;

    private String member;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.key() + ":%s(db%s) " + I18nHelper.memberAdded() + ":%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.member
        );
    }

}
