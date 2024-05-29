package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisInfoTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.INFO;
    }

    @Override
    public String commandArg() {
        return "[section]";
    }

    @Override
    public String commandDesc() {
        return "服务信息";
    }
}
