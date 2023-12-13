package cn.oyzh.easyredis.terminal.handler.hash;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.hash.RedisHdelTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisHdelCommandHandler extends RedisKeyTerminalCommandHandler<RedisHdelTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisHdelTerminalCommand parseCommand(String line, String[] words) {
        RedisHdelTerminalCommand command = new RedisHdelTerminalCommand();
        command.key(words[1]);
        command.fields(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHdelTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().hdel(null, command.key(), command.fields());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HDEL";
    }

    @Override
    public String commandArg() {
        return "key field [field ...]";
    }

    @Override
    public String commandDesc() {
        return "删除hash字段";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
