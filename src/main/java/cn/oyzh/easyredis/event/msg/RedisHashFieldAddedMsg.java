package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisHashFieldAddedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_HASH_FIELD_ADDED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisHashKeyTreeItem item;

    @Setter
    private String key;

    @Setter
    private String field;

    @Setter
    private String value;

    @Override
    public String formatMsg() {
        return String.format(
                "[%s] 键:%s(db%s) 新增字段:%s 值:%s",
                this.item.infoName(), this.key, this.item.dbIndex(), this.field, this.value
        );
    }
}
