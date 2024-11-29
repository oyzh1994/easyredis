package cn.oyzh.easyredis.terminal.base;

import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
public class RedisMoveTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key db";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.MOVE;
    }
}
