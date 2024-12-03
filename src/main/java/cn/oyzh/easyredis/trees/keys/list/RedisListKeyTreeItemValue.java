package cn.oyzh.easyredis.trees.keys.list;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisListKeyTreeItemValue extends RedisKeyTreeItemValue<RedisListKeyTreeItem> {

    public RedisListKeyTreeItemValue(RedisListKeyTreeItem item) {
        super(item);
        // item.dataProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
    }
}
