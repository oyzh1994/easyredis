package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisBlpopTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.BLPOP;
    }

    @Override
    public String commandArg() {
        return "timeout key [key...]";
    }

    @Override
    public String commandDesc() {
        return "从list头部弹出值，直到超时";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
