package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
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
    public RedisDatabaseTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisDatabaseTreeItem) parent;
    }

    public RedisConnect redisConnect(){
        return this.parent().redisConnect();
    }

    @Override
    public void onPrimaryDoubleClick() {
    }

}
