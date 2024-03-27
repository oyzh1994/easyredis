package cn.oyzh.easyredis.trees.list;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisListKeyTreeItemValue extends RedisKeyTreeItemValue<RedisListKeyTreeItem> {

    public RedisListKeyTreeItemValue(RedisListKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((_, _, _) -> this.flushGraphicColor());
    }
}
