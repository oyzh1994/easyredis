package cn.oyzh.easyredis.terminal.handler.hyperloglog;

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
public class RedisPfmergeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {


    @Override
    public String commandArg() {
        return "destkey sourcekey [sourcekey...]";
    }

    @Override
    public String commandDesc() {
        return "合并多个hyperloglog";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.PFMERGE;
    }
}
