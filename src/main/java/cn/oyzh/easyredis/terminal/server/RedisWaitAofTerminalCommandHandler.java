package cn.oyzh.easyredis.terminal.server;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisWaitAofTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.WAITAOF;
    }

    @Override
    public String commandArg() {
        return "numlocal numreplicas timeout";
    }

    @Override
    public String commandDesc() {
        return "等待副本保存";
    }
}
