package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.msg.RedisTerminalCloseMsg;
import cn.oyzh.easyredis.event.msg.RedisTerminalOpenMsg;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventBuilder;
import cn.oyzh.fx.plus.event.EventUtil;
import lombok.experimental.UtilityClass;

/**
 * zk事件工具
 *
 * @author oyzh
 * @since 2023/11/20
 */
@UtilityClass
public class RedisEventUtil {

    /**
     * 终端打开事件
     */
    public static void terminalOpen() {
        terminalOpen(null);
    }

    /**
     * 终端打开事件
     *
     * @param info redis信息
     */
    public static void terminalOpen(RedisInfo info) {
        RedisTerminalOpenMsg msg = new RedisTerminalOpenMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 终端关闭事件
     *
     * @param info redis信息
     */
    public static void terminalClose(RedisInfo info) {
        RedisTerminalCloseMsg msg = new RedisTerminalCloseMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

}
