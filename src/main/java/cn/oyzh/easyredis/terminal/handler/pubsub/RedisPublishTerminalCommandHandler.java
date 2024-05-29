package cn.oyzh.easyredis.terminal.handler.pubsub;

import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/08/02
 */
@Component
public class RedisPublishTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "channel message";
    }

    @Override
    public String commandDesc() {
        return "发布消息";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.PUBLISH;
    }
}
