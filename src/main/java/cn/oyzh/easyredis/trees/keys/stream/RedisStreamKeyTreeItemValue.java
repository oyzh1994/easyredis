package cn.oyzh.easyredis.trees.keys.stream;

import cn.oyzh.easyredis.trees.keys.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStreamKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStreamKeyTreeItem> {

    public RedisStreamKeyTreeItemValue(RedisStreamKeyTreeItem item) {
        super(item);
    }
}
