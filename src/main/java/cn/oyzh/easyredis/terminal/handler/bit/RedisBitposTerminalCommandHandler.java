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
public class RedisBitposTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key bit [start [end [BYTE | BIT]]]";
    }

    @Override
    public String commandDesc() {
        return "获取bit值首次出现的位置";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.BITPOS;
    }
}
