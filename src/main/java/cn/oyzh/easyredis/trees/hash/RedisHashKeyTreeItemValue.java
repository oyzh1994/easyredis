package cn.oyzh.easyredis.trees.hash;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisHashKeyTreeItemValue extends RedisKeyTreeItemValue<RedisHashKeyTreeItem> {

    public RedisHashKeyTreeItemValue(RedisHashKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((observableValue, o, t1) -> this.flushGraphicColor());
    }
}
