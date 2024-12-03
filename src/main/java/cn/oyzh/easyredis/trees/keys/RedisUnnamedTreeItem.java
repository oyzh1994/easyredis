package cn.oyzh.easyredis.trees.keys;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import lombok.NonNull;

/**
 * @author oyzh
 * @since 2024-12-03
 */
public class RedisUnnamedTreeItem extends RichTreeItem<RedisUnnamedTreeItem.RedisUnnamedTreeItemValue> {

    public RedisUnnamedTreeItem(@NonNull RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisUnnamedTreeItemValue());
    }

    public static class RedisUnnamedTreeItemValue extends RichTreeItemValue {

        @Override
        public String name() {
            return "库列表";
        }
    }
}
