package cn.oyzh.easyredis.shell.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.string.RedisGetrangeTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisGetrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGetrangeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisGetrangeTerminalCommand parseCommand(String line, String[] words) {
        RedisGetrangeTerminalCommand command = new RedisGetrangeTerminalCommand();
        command.key(words[1]);
        command.startOffset(Long.parseLong(words[2]));
        command.endOffset(Long.parseLong(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGetrangeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String value = terminal.client().getrange(null, command.key(), command.startOffset(), command.endOffset());
            result.setResult(RedisShellUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GETRANGE";
    }

    @Override
    public String commandArg() {
        return "key startOffset endOffset";
    }

    @Override
    public String commandDesc() {
        return "获取string指定区间值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
