package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeView;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/06/27
 */
//@Slf4j
public abstract class RedisTreeItem<V extends RedisTreeItemValue> extends RichTreeItem<V> {

    public RedisTreeItem(RichTreeView treeView) {
        super(treeView);
    }

    @Override
    public RedisTreeView getTreeView() {
        return (RedisTreeView) super.getTreeView();
    }
}
