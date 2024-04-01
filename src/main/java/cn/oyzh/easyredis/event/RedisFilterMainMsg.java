package cn.oyzh.easyredis.event;

import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class RedisFilterMainMsg extends Event<Object> {

}
