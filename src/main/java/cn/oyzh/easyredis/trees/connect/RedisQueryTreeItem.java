package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisQueryTreeItem extends RichTreeItem<RedisQueryTreeItemValue> {

    public RedisQueryTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisQueryTreeItemValue());
    }

    @Override
    public RedisConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
    }
}
