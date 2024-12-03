package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisTerminalTreeItem extends RichTreeItem<RedisTerminalTreeItemValue> {

    public RedisTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisTerminalTreeItemValue());
    }

    @Override
    public RedisConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        RedisEventUtil.terminalOpen(this.parent().value());
    }
}
