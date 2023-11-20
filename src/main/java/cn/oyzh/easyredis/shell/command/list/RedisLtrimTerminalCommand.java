package cn.oyzh.easyredis.shell.command.list;

import cn.oyzh.easyredis.shell.command.RedisKeyTerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisLtrimTerminalCommand extends RedisKeyTerminalCommand {

    private long start;

    private long stop;
}
