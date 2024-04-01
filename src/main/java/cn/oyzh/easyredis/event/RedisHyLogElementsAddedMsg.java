package cn.oyzh.easyredis.event;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Getter
@Accessors(fluent = true)
public class RedisHyLogElementsAddedMsg extends Event<RedisStringKeyTreeItem> implements  EventFormatter {

    @Setter
    private String key;

    @Setter
    private String[] elements;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键:%s(db%s) 新增统计元素:%s",
                this.data().infoName(), this.key, this.data().dbIndex(), ArrayUtil.toString(this.elements)
        );
    }
}
