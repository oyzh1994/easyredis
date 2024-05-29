package cn.oyzh.easyredis.terminal.bit;

import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisGetbitTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key offset";
    }

    @Override
    public String commandDesc() {
        return "获取bit值";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.GETBIT;
    }
}
