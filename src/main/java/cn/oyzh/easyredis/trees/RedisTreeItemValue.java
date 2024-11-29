package cn.oyzh.easyredis.trees;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import lombok.experimental.Accessors;


/**
 * redis树键值
 *
 * @author oyzh
 * @since 2023/07/7
 */
@Accessors(chain = true, fluent = true)
public class RedisTreeItemValue extends RichTreeItemValue {

    public RedisTreeItemValue() {
        super();
    }

    public RedisTreeItemValue(RichTreeItem<?> item) {
        super(item);
    }

}
