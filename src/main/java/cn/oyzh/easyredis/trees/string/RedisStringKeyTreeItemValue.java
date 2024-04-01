package cn.oyzh.easyredis.trees.string;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStringKeyTreeItem> {

    public RedisStringKeyTreeItemValue(RedisStringKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
    }
}
