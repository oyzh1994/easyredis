package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.handler.RedisNKeysTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisDelTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.DEL;
    }

    @Override
    public String commandDesc() {
        return "删除键";
    }

}
