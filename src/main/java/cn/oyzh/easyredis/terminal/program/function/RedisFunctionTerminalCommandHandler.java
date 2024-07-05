package cn.oyzh.easyredis.terminal.program.function;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisFunctionTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.FUNCTION;
    }

    @Override
    public String commandHelp(RedisTerminalTextTextArea terminal) {
        CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), "HELP");
        Object obj = terminal.client().execCommand(object);
        return RedisTerminalUtil.formatOut(obj);
    }
}
