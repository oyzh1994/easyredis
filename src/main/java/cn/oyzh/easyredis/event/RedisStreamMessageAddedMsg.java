package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
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
public class RedisStreamMessageAddedMsg extends Event<RedisStreamKeyTreeItem> implements  EventFormatter {

    @Setter
    private String key;

    @Setter
    private String message;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] 键:%s(db%s) 新增消息:%s",
                this.data().infoName(), this.key, this.data().dbIndex(), this.message
        );
    }
}
