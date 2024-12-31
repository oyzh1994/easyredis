package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;
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
public class RedisKeyCopiedEvent extends Event<TreeItem<?>> implements EventFormatter {

    private int targetDB;

    @Override
    public String eventFormat() {
        if (this.data() instanceof RedisKeyTreeItem treeItem) {
            return String.format(
                    "[%s] " + I18nHelper.copyKey() + "[%s-db%s] " + I18nHelper.targetDatabase() + ":%s",
                    treeItem.redisConnect().getName(), treeItem.key(), treeItem.dbIndex(), this.targetDB
            );
        }
        return null;
    }
}
