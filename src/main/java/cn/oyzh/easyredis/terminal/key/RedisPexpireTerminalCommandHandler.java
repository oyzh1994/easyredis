package cn.oyzh.easyredis.terminal.key;

import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisPexpireTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.PEXPIRE;
    }

    @Override
    public String commandArg() {
        return "key milliseconds [condition]";
    }

    @Override
    public String commandDesc() {
        return "设置过期时间，以毫秒为单位";
    }

}
