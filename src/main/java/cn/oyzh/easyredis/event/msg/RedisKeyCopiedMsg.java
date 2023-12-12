package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.RedisKeyTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
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
public class RedisKeyCopiedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_KEY_COPIED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private TreeItem<?> item;

    @Setter
    private int targetDB;

    @Override
    public String formatMsg() {
        if (item instanceof RedisKeyTreeItem<?, ?> treeItem) {
            return String.format(
                    "[%s] 键复制[%s-db%s] 目标库:%s",
                    treeItem.info().getName(), treeItem.key(), treeItem.dbIndex(), this.targetDB
            );
        }
        return null;
    }
}
