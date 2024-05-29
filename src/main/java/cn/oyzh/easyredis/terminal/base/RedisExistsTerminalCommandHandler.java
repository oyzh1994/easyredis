package cn.oyzh.easyredis.terminal.base;

import cn.oyzh.easyredis.terminal.RedisNKeysTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisExistsTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandDesc() {
        return "键是否存在";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.EXISTS;
    }
}
