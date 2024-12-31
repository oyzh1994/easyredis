package cn.oyzh.easyredis.event.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = false,fluent = true)
public class RedisTerminalOpenEvent extends Event<RedisConnect> {

    private Integer dbIndex;

}
