package cn.oyzh.easyredis.trees.keys.set;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisSetKeyTreeItem> {

    public RedisSetKeyTreeItemValue(RedisSetKeyTreeItem item) {
        super(item);
        // item.dataProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
    }
}
