package cn.oyzh.easyredis.terminal.handler.connect;

import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Component
public class RedisPingTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandDesc() {
        return "检测连接";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.PING;
    }
}
