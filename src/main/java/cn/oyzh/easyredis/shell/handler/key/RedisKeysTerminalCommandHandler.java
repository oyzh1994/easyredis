package cn.oyzh.easyredis.shell.handler.key;

import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.key.RedisKeysTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisKeysTerminalCommandHandler extends RedisTerminalCommandHandler<RedisKeysTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    protected RedisKeysTerminalCommand parseCommand(String line, String[] words) {
        RedisKeysTerminalCommand command = new RedisKeysTerminalCommand();
        command.pattern(words[1]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisKeysTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Set<String> keys = terminal.client().keys((Integer) null, command.pattern());
            result.setResult(RedisShellUtil.formatOut(keys));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "KEYS";
    }

    @Override
    public String commandArg() {
        return "pattern";
    }

    @Override
    public String commandDesc() {
        return "列举键";
    }

}
