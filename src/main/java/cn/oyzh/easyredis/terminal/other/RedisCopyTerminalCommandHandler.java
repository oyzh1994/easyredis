package cn.oyzh.easyredis.terminal.other;

import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisCopyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "source destination [DB destination-db] [REPLACE]";
    }

    @Override
    public String commandDesc() {
        return "复制键";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.COPY;
    }
}
