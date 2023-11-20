package cn.oyzh.easyredis.shell.command.list;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import redis.clients.jedis.args.ListPosition;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisLinsertTerminalCommand extends RedisKeyTerminalCommand {

    private ListPosition where = ListPosition.BEFORE;

    private String pivot;

    private String value;

    public void whereOfString(String where) {
        if (StrUtil.equalsIgnoreCase(where, "AFTER")) {
            this.where = ListPosition.AFTER;
        }
    }
}
