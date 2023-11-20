package cn.oyzh.easyredis.shell.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.string.RedisAppendCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisAppendTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisAppendCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisAppendCommand parseCommand(String line, String[] words) {
        RedisAppendCommand command = new RedisAppendCommand();
        command.key(words[1]);
        command.value(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisAppendCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().append(null, command.key(), command.value());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "APPEND";
    }

    @Override
    public String commandArg() {
        return "key value";
    }

    @Override
    public String commandDesc() {
        return "追加string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
