package cn.oyzh.easyredis.terminal.other;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisTimeTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandDesc() {
        return "获取服务器时间";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.TIME;
    }
}
