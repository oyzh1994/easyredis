package cn.oyzh.easyredis.terminal.command.hyperloglog;

import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisPfcountTerminalCommand extends RedisKeyTerminalCommand {

    private String[] keys;
}
