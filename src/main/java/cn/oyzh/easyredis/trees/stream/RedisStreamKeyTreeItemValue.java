package cn.oyzh.easyredis.trees.stream;

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
public class RedisStreamKeyTreeItemValue extends RedisKeyTreeItemValue<RedisStreamKeyTreeItem> {

    public RedisStreamKeyTreeItemValue(RedisStreamKeyTreeItem item) {
        super(item);
    }
}
