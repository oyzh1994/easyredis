package cn.oyzh.easyredis.trees.string;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import javafx.beans.value.WeakChangeListener;
import lombok.extern.slf4j.Slf4j;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
@Slf4j
public class RedisStringKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStringKeyTreeItem> {

    public RedisStringKeyTreeItemValue(RedisStringKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener(new WeakChangeListener<>((observableValue, o, t1) -> this.flushGraphicColor()));
    }
}
