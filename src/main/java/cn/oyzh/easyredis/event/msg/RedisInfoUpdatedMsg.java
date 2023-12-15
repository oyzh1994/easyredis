package cn.oyzh.easyredis.event.msg;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventGroups;
import cn.oyzh.easyredis.event.RedisEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/08
 */
@Getter
@Accessors(fluent = true)
public class RedisInfoUpdatedMsg implements EventMsg, EventMsgFormatter {

    @Getter
    private final String name = RedisEventTypes.REDIS_INFO_UPDATED;

    @Getter
    private final String group = RedisEventGroups.INFO_ACTION;

    @Setter
    private RedisInfo info;

    public String formatMsg() {
        return String.format("连接[%s] 已修改", this.info.getName());
    }
}
