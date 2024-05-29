package cn.oyzh.easyredis.terminal.handler.stream;

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
public class RedisXrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key start end [count]";
    }

    @Override
    public String commandDesc() {
        return "获取stream区间内的消息";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.XRANGE;
    }
}
