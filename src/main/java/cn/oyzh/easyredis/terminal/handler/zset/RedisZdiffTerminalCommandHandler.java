package cn.oyzh.easyredis.terminal.handler.zset;

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
public class RedisZdiffTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "numkeys key [key ...] [WITHSCORES]";
    }

    @Override
    public String commandDesc() {
        return "获取多个zset的差集";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.ZDIFF;
    }
}
