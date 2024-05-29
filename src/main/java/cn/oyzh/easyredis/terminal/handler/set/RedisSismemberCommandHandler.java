package cn.oyzh.easyredis.terminal.handler.set;

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
public class RedisSismemberCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key member";
    }

    @Override
    public String commandDesc() {
        return "是否set成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.SISMEMBER;
    }
}
