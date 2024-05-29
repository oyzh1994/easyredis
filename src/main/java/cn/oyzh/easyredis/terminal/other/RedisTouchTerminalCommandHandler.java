package cn.oyzh.easyredis.terminal.other;

import cn.oyzh.easyredis.terminal.RedisNKeysTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisTouchTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.TOUCH;
    }

    @Override
    public String commandArg() {
        return "key [key ...]";
    }

    @Override
    public String commandDesc() {
        return "修改键的最后访问时间";
    }

}
