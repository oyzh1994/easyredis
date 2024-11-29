package cn.oyzh.easyredis.trees.zset;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;


/**
 * Redis zset树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisZSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTreeItemValue(RedisZSetKeyTreeItem item) {
        super(item);
        // item.dataProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
        // item.scoreProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
        // item.latitudeProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
        // item.longitudeProperty().addListener((t1, t2, t3) -> this.flushGraphicColor());
    }
}
