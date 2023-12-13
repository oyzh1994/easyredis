package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisIncrbyCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisIncrbyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisIncrbyCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisIncrbyCommand parseCommand(String line, String[] words) {
        RedisIncrbyCommand command = new RedisIncrbyCommand();
        command.key(words[1]);
        command.increment(Long.parseLong(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisIncrbyCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long value = terminal.client().incrBy(null, command.key(), command.increment());
            result.setResult(RedisTerminalUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "INCRBY";
    }

    @Override
    public String commandArg() {
        return "key increment";
    }

    @Override
    public String commandDesc() {
        return "增加string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
