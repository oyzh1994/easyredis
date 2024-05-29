package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisPexpireAtTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.PEXPIREAT;
    }

    @Override
    public String commandArg() {
        return "key millisecondsTimestamp [condition]";
    }

    @Override
    public String commandDesc() {
        return "设置到期期时间，以毫秒为单位";
    }

}
