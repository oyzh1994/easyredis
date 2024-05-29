package cn.oyzh.easyredis.terminal.handler.connect;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/12/13
 */
@Component
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
    public String commandDesc() {
        return "开始连接";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
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
