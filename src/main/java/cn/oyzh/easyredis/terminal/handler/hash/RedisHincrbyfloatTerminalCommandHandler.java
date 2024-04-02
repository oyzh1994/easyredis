package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.hash.RedisHincrbyfloatCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/28
 */
@Component
public class RedisHincrbyfloatTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisHincrbyfloatCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisHincrbyfloatCommand parseCommand(String line, String[] words) {
        RedisHincrbyfloatCommand command = new RedisHincrbyfloatCommand();
        command.key(words[1]);
        command.field(words[2]);
        command.increment(Double.parseDouble(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHincrbyfloatCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            double value = terminal.client().hincrByFloat(null, command.key(), command.field(), command.increment());
            result.setResult(RedisTerminalUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HINCRBYFLOAT";
    }

    @Override
    public String commandArg() {
        return "key field increment";
    }

    @Override
    public String commandDesc() {
        return "增加hash字段值，以浮点形式";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
