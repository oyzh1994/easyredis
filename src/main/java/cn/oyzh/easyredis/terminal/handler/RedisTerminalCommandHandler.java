package cn.oyzh.easyredis.terminal.handler;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.util.RedisVersionUtil;
import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;

/**
 * @author oyzh
 * @since 2023/7/31
 */
public abstract class RedisTerminalCommandHandler<C extends TerminalCommand> extends BaseTerminalCommandHandler<C, RedisTerminalTextArea> {

    @Override
    public String commandSupportedVersion() {
        return RedisVersionUtil.getSupportedVersion(this.commandName());
    }
}
