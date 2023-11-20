package cn.oyzh.easyredis.shell.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hash.RedisHincrbyCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHincrbyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisHincrbyCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisHincrbyCommand parseCommand(String line, String[] words) {
        RedisHincrbyCommand command = new RedisHincrbyCommand();
        command.key(words[1]);
        command.field(words[2]);
        command.increment(Long.parseLong(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHincrbyCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long value = terminal.client().hincrBy(null, command.key(), command.field(), command.increment());
            result.setResult(RedisShellUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HINCRBY";
    }

    @Override
    public String commandArg() {
        return "key field increment";
    }

    @Override
    public String commandDesc() {
        return "增加hash字段值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
