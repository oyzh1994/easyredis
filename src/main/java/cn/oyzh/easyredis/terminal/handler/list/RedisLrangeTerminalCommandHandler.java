package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisLrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.LRANGE;
    }

    @Override
    public String commandArg() {
        return "key start end";
    }

    @Override
    public String commandDesc() {
        return "获取list指定区间内的值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
