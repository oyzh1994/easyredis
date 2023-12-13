package cn.oyzh.easyredis.terminal.command.server;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/7/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisSlowlogGetTerminalCommand extends TerminalCommand {

    private Long entries;
}
