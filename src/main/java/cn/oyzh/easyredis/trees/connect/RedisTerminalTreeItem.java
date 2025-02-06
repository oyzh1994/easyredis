package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisTerminalTreeItem extends RichTreeItem<RedisTerminalTreeItemValue> {

    private final Integer dbIndex;

    public RedisTerminalTreeItem(RichTreeView treeView, Integer dbIndex) {
        super(treeView);
        this.dbIndex = dbIndex;
        this.setValue(new RedisTerminalTreeItemValue());
    }

    public RedisConnect redisConnect() {
        if (this.parent() instanceof RedisDatabaseTreeItem item) {
            return item.redisConnect();
        }
        if (this.parent() instanceof RedisConnectTreeItem item) {
            return item.value();
        }
        return null;
    }

    public RedisClient client() {
        if (this.parent() instanceof RedisDatabaseTreeItem item) {
            return item.client();
        }
        if (this.parent() instanceof RedisConnectTreeItem item) {
            return item.client();
        }
        return null;
    }

    @Override
    public void onPrimaryDoubleClick() {
        RedisEventUtil.terminalOpen(this.client(), this.dbIndex);
    }

}
