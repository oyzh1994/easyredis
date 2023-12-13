package cn.oyzh.easyredis.terminal.command.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisWaitAofTerminalCommand extends RedisWaitTerminalCommand {

    private int numLocal;
}
