package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.string.RedisSetrangeTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSetrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSetrangeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisSetrangeTerminalCommand parseCommand(String line, String[] words) {
        RedisSetrangeTerminalCommand command = new RedisSetrangeTerminalCommand();
        command.key(words[1]);
        command.offset(Long.parseLong(words[2]));
        command.value(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSetrangeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().setrange(null, command.key(), command.offset(), command.value());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SETRANGE";
    }

    @Override
    public String commandArg() {
        return "key offset value";
    }

    @Override
    public String commandDesc() {
        return "设置string指定位置值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
