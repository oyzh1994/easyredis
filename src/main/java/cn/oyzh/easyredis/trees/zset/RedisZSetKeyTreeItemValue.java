package cn.oyzh.easyredis.trees.zset;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis zset树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisZSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTreeItemValue(RedisZSetKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener((_, _, _) -> this.flushGraphicColor());
        item.scoreProperty().addListener((_, _, _) -> this.flushGraphicColor());
        item.latitudeProperty().addListener((_, _, _) -> this.flushGraphicColor());
        item.longitudeProperty().addListener((_, _, _) -> this.flushGraphicColor());
    }
}
