package cn.oyzh.easyredis.terminal.command.set;

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
public class RedisSdiffTerminalCommand extends TerminalCommand {

    private String[] keys;

    private boolean withScores;

}
