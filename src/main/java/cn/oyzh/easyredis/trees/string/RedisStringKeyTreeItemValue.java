package cn.oyzh.easyredis.trees.string;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisStringKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStringKeyTreeItem> {

    public RedisStringKeyTreeItemValue(RedisStringKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((observableValue, o, t1) -> this.flushGraphicColor());
    }
}
