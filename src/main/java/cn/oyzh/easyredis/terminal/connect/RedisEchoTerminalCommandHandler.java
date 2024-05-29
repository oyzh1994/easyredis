package cn.oyzh.easyredis.terminal.connect;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Component
public class RedisEchoTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandDesc() {
        return "打印内容";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.ECHO;
    }
}
