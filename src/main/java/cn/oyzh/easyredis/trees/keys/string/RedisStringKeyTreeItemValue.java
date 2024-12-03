package cn.oyzh.easyredis.trees.keys.string;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStringKeyTreeItem> {

    public RedisStringKeyTreeItemValue(RedisStringKeyTreeItem item) {
        super(item);
    }

}
