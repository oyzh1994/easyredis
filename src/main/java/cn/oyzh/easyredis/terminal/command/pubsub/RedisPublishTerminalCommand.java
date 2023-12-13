package cn.oyzh.easyredis.terminal.command.pubsub;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/8/02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class RedisPublishTerminalCommand extends TerminalCommand {

    private String channel;

    private String message;

}
