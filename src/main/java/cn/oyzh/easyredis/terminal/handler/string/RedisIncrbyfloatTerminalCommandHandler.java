package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisIncrbyfloatCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisIncrbyfloatTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisIncrbyfloatCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisIncrbyfloatCommand parseCommand(String line, String[] words) {
        RedisIncrbyfloatCommand command = new RedisIncrbyfloatCommand();
        command.key(words[1]);
        command.increment(Double.parseDouble(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisIncrbyfloatCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            double value = terminal.client().incrByFloat(null, command.key(), command.increment());
            result.setResult(RedisTerminalUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "INCRBYFLOAT";
    }

    @Override
    public String commandArg() {
        return "key increment";
    }

    @Override
    public String commandDesc() {
        return "增加string值，以浮点形式";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
