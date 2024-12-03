package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisDataTreeItem extends RichTreeItem<RedisDataTreeItemValue> {

    public RedisDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisDataTreeItemValue());
    }

    @Override
    public RedisConnectTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (RedisConnectTreeItem) treeItem;
    }

    @Override
    public void onPrimaryDoubleClick() {
        super.startWaiting(() -> RedisEventUtil.connectionOpened(this.parent()));
    }
}
