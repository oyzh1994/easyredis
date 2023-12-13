package cn.oyzh.easyredis.terminal.handler.connect;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.connect.RedisEchoTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Component
public class RedisEchoTerminalCommandHandler extends RedisTerminalCommandHandler<RedisEchoTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    protected RedisEchoTerminalCommand parseCommand(String line, String[] words) {
        RedisEchoTerminalCommand command = new RedisEchoTerminalCommand();
        command.echoMsg(words[1]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisEchoTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String echo = terminal.client().echo(command.echoMsg());
            result.setResult(RedisShellUtil.formatOut(echo));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ECHO";
    }

    @Override
    public String commandDesc() {
        return "打印内容";
    }
}
