package cn.oyzh.easyredis.terminal.command.key;

import cn.oyzh.fx.terminal.command.TerminalCommand;
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
public class RedisKeysTerminalCommand extends TerminalCommand {

    private String pattern;

}
