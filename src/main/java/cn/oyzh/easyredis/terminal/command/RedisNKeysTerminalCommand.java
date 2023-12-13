package cn.oyzh.easyredis.terminal.command;

import cn.oyzh.fx.terminal.command.TerminalCommand;
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
public class RedisNKeysTerminalCommand extends TerminalCommand {

    private String[] keys;
}
