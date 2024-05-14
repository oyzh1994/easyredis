package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/12
 */
@Getter
@Accessors(fluent = true)
public class RedisKeyCopiedEvent extends Event<TreeItem<?>> implements EventFormatter {

    @Setter
    private int targetDB;

    @Override
    public String eventFormat() {
        if (this.data() instanceof RedisKeyTreeItem<?, ?> treeItem) {
            return String.format(
                    "[%s] " + I18nHelper.copyKey() + "[%s-db%s] " + I18nHelper.targetDatabase() + ":%s",
                    treeItem.info().getName(), treeItem.key(), treeItem.dbIndex(), this.targetDB
            );
        }
        return null;
    }
}
