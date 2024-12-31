package cn.oyzh.easyredis.terminal.base;

import cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler;
import cn.oyzh.easyredis.terminal.RedisTerminalTextTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/12/13
 */

public class RedisConnectTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected TerminalCommand parseCommand(String line, String[] args) {
        TerminalCommand terminalCommand = new TerminalCommand();
        terminalCommand.args(args);
        terminalCommand.command(line);
        return terminalCommand;
    }

    @Override
    protected boolean checkArgs(String[] args) throws RuntimeException {
        return args != null;
    }

    @Override
    public String commandName() {
        return "connect";
    }

    @Override
    protected Protocol.Command getCommandType() {
        return null;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextTextArea terminal) {
        if (terminal.isTemporary()) {
            if (terminal.isConnected()) {
                terminal.client().close();
            }
            terminal.connect(command.command());
        } else {
            terminal.outputByPrompt("非临时连接不支持此操作");
        }
        TerminalExecuteResult result = TerminalExecuteResult.ok();
        result.setIgnoreOutput(true);
        return result;
    }
}
