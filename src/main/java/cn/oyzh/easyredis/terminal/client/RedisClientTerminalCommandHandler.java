package cn.oyzh.easyredis.terminal.client;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextAreaPane;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */

public class RedisClientTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public Protocol.Command getCommandType() {
        return Protocol.Command.CLIENT;
    }

    @Override
    public String commandHelp(RedisTerminalTextAreaPane terminal) {
        CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), "HELP");
        Object obj = terminal.getClient().execCommand(object);
        return RedisTerminalUtil.formatOut(obj);
    }
}
