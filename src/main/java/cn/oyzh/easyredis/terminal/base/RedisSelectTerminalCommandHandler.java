package cn.oyzh.easyredis.terminal.base;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisSelectTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "db";
    }

    @Override
    public String commandDesc() {
        return "切换数据库";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.SELECT;
    }
}
