package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisMoveTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key db";
    }

    @Override
    public String commandDesc() {
        return "移动键";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.MOVE;
    }
}
