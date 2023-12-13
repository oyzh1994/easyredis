package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisGetsetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisGetsetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGetsetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisGetsetTerminalCommand parseCommand(String line, String[] words) {
        RedisGetsetTerminalCommand command = new RedisGetsetTerminalCommand();
        command.key(words[1]);
        command.value(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGetsetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String value = terminal.client().getSet(null, command.key(), command.value());
            result.setResult(RedisShellUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GETSET";
    }

    @Override
    public String commandArg() {
        return "key value";
    }

    @Override
    public String commandDesc() {
        return "设置string值，并返回旧值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
