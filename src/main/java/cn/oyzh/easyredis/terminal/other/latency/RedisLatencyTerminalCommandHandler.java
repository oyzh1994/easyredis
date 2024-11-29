package cn.oyzh.easyredis.terminal.other.latency;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisLatencyTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.LATENCY;
    }

    @Override
    public String commandHelp(RedisTerminalTextTextArea terminal) {
        CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), "HELP");
        Object obj = terminal.client().execCommand(object);
        return RedisTerminalUtil.formatOut(obj);
    }
}
