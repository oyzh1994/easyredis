package cn.oyzh.easyredis.terminal.stream.xinfo;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisKeyTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisXinfoTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.XINFO;
    }

    @Override
    public String commandHelp(RedisTerminalTextTextArea terminal) {
        CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), "HELP");
        Object obj = terminal.client().execCommand(object);
        return RedisTerminalUtil.formatOut(obj);
    }
}
