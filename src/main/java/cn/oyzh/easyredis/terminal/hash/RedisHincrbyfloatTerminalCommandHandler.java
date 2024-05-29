package cn.oyzh.easyredis.terminal.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/28
 */
@Component
public class RedisHincrbyfloatTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key field increment";
    }

    @Override
    public String commandDesc() {
        return "增加hash字段值，以浮点形式";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.HINCRBYFLOAT;
    }
}
