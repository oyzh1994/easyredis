package cn.oyzh.easyredis.shell.command.zset;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/08/01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisZdiffTerminalCommand extends TerminalCommand {

    private int numkeys;

    private String[] keys;

    private boolean withScores;

}
