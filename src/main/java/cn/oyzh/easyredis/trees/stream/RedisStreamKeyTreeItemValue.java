package cn.oyzh.easyredis.trees.stream;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisStreamKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStreamKeyTreeItem> {

    public RedisStreamKeyTreeItemValue(RedisStreamKeyTreeItem item) {
        super(item);
    }
}
