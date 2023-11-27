package cn.oyzh.easyredis.trees.set;

import cn.oyzh.easyredis.trees.RedisKeyTreeItemValue;
import javafx.beans.value.WeakChangeListener;


/**
 * Redis set树节点值
 *
 * @author oyzh
 * @since 2023/11/21
 */
//@Slf4j
public class RedisSetKeyTreeItemValue extends RedisKeyTreeItemValue<RedisSetKeyTreeItem> {

    public RedisSetKeyTreeItemValue(RedisSetKeyTreeItem item) {
        super(item);
        item.dataProperty().addListener(new WeakChangeListener<>((observableValue, o, t1) -> this.flushGraphicColor()));
    }
}
