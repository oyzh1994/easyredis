package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisLpushTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.LPUSH;
    }

    @Override
    public String commandArg() {
        return "key value [value ...]";
    }

    @Override
    public String commandDesc() {
        return "从左侧推送数据到list";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
