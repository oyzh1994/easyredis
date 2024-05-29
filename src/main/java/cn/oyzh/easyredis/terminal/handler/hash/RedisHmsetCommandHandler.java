package cn.oyzh.easyredis.terminal.handler.hash;

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
public class RedisHmsetCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key field value [field value ...]";
    }

    @Override
    public String commandDesc() {
        return "设置hash多个字段";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.HMSET;
    }
}
