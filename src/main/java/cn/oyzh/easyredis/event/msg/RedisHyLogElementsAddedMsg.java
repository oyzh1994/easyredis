package cn.oyzh.easyredis.event.msg;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
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
public class RedisHyLogElementsAddedMsg implements EventMsg, EventMsgFormatter {

    private final String name = RedisEventTypes.REDIS_HYLOG_ELEMENT_ADDED;

    private final String group = RedisEventGroups.KEY_ACTION;

    @Setter
    private RedisStringKeyTreeItem item;

    @Setter
    private String key;

    @Setter
    private String[] elements;

    @Override
    public String formatMsg() {
        return String.format(
                "[%s] 键:%s(db%s) 新增统计元素:%s",
                this.item.infoName(), this.key, this.item.dbIndex(), ArrayUtil.toString(this.elements)
        );
    }
}
