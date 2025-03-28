package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/12/11
 */
public class RedisKeyRenamedEvent extends Event<RedisKeyTreeItem> implements EventFormatter {

    private String oldKey;

    public String getOldKey() {
        return oldKey;
    }

    public void setOldKey(String oldKey) {
        this.oldKey = oldKey;
    }

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.keyRenamed() + "[%s-db%s] " + I18nHelper.newName() + ":%s",
                this.data().redisConnect().getName(), this.oldKey, this.data().dbIndex(), this.data().key()
        );
    }
}
