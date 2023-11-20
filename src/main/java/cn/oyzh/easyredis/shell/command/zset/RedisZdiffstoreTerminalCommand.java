package cn.oyzh.easyredis.shell.command.zset;

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
public class RedisZdiffstoreTerminalCommand extends RedisKeyTerminalCommand {

    private int numkeys;

    private String[] keys;
}
