package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.gui.treeView.RichTreeView;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/06/27
 */
public abstract class RedisTreeItem<V extends RichTreeItemValue> extends RichTreeItem<V> {

    public RedisTreeItem(RichTreeView treeView) {
        super(treeView);
    }

    @Override
    public RedisTreeView getTreeView() {
        return (RedisTreeView) super.getTreeView();
    }
}
