package cn.oyzh.easyredis.terminal.program;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisScriptTerminalCommandHandler extends cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.SCRIPT;
    }
}
