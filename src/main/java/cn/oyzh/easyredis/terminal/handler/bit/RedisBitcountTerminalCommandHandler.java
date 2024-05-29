package cn.oyzh.easyredis.terminal.handler.bit;

import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisBitcountTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key [start end [BYTE | BIT]]";
    }

    @Override
    public String commandDesc() {
        return "统计bit值";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.BITCOUNT;
    }
}
