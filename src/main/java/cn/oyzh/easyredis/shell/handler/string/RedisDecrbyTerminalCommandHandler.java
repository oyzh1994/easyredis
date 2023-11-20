package cn.oyzh.easyredis.shell.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.string.RedisDecrbyCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisDecrbyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisDecrbyCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisDecrbyCommand parseCommand(String line, String[] words) {
        RedisDecrbyCommand command = new RedisDecrbyCommand();
        command.key(words[1]);
        command.decrement(Long.parseLong(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisDecrbyCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long value = terminal.client().decrBy(null, command.key(), command.decrement());
            result.setResult(RedisShellUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "DECRBY";
    }

    @Override
    public String commandArg() {
        return "key decrement";
    }

    @Override
    public String commandDesc() {
        return "减少string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
