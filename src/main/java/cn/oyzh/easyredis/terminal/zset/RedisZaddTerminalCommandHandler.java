package cn.oyzh.easyredis.terminal.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZaddTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {


    @Override
    public String commandArg() {
        return "key score member [score member...]";
    }

    @Override
    public String commandDesc() {
        return "添加zset成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.ZADD;
    }
}
