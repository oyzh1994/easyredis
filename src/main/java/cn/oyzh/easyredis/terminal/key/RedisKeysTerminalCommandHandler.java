package cn.oyzh.easyredis.terminal.key;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisKeysTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "pattern";
    }

    @Override
    public String commandDesc() {
        return "列举键";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.KEYS;
    }

}
