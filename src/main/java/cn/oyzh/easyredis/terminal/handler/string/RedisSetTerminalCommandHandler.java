package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisSetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisSetTerminalCommand parseCommand(String line, String[] words) {
        RedisSetTerminalCommand command = new RedisSetTerminalCommand();
        command.key(words[1]);
        command.value(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().set(null, command.key(), command.value());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SET";
    }

    @Override
    public String commandArg() {
        return "key value";
    }

    @Override
    public String commandDesc() {
        return "设置string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
