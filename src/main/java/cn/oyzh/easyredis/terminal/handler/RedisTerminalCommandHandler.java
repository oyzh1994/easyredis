package cn.oyzh.easyredis.terminal.handler;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.util.RedisVersionUtil;
import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */
public abstract class RedisTerminalCommandHandler<C extends TerminalCommand> extends BaseTerminalCommandHandler<C, RedisTerminalTextArea> {

    @Override
    public String commandSupportedVersion() {
        return RedisVersionUtil.getSupportedVersion(this.commandName());
    }

    @Override
    public TerminalExecuteResult execute(C command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            CommandObject<Object> object = RedisTerminalUtil.getCommand(this.getCommandType(), command);
            Object obj = terminal.client().execCommand(object);
            result.setResult(RedisTerminalUtil.formatOut(obj));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return this.getCommandType().name();
    }

    protected abstract Protocol.Command getCommandType();
}
