package cn.oyzh.easyredis.trees.set;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisSetKeyTreeItem> {

    public RedisSetKeyTreeItemValue(RedisSetKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((_, _, _) -> this.flushGraphicColor());
    }
}
