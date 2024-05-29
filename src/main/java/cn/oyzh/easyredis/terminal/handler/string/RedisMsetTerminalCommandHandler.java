package cn.oyzh.easyredis.terminal.handler.string;

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
public class RedisMsetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {
    @Override
    public String commandArg() {
        return "key value [key value ...]";
    }

    @Override
    public String commandDesc() {
        return "设置多个string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.MSET;
    }
}
