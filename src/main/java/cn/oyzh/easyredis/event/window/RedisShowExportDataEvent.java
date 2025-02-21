package cn.oyzh.easyredis.event.window;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2025-02-20
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisShowExportDataEvent extends Event<RedisConnect> {

    private String path;
}
