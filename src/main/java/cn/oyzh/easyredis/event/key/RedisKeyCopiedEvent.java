package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.trees.key.RedisKeyTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/12/12
 */
public class RedisKeyCopiedEvent extends Event<TreeItem<?>> implements EventFormatter {

    private int targetDB;

    public int getTargetDB() {
        return targetDB;
    }

    public void setTargetDB(int targetDB) {
        this.targetDB = targetDB;
    }

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
