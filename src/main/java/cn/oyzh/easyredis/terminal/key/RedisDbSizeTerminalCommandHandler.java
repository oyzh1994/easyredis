package cn.oyzh.easyredis.terminal.key;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Component
public class RedisDbSizeTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.DBSIZE;
    }

    @Override
    public String commandDesc() {
        return "获取键数量";
    }
}
