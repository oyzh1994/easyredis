package cn.oyzh.easyredis.terminal.module;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextAreaPane;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisModuleTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public Protocol.Command getCommandType() {
        return Protocol.Command.MODULE;
    }

    @Override
    public String commandHelp(RedisTerminalTextAreaPane terminal) {
        CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), "HELP");
        Object obj = terminal.getClient().execCommand(object);
        return RedisTerminalUtil.formatOut(obj);
    }
}
