package cn.oyzh.easyredis.shell.command.pubsub;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/08/03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisPubsubChannelsTerminalCommand extends TerminalCommand {

    private String pattern;

}
