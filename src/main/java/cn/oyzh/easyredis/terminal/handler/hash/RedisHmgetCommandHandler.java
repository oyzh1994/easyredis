package cn.oyzh.easyredis.terminal.handler.hash;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.hash.RedisHmgetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisHmgetCommandHandler extends RedisKeyTerminalCommandHandler<RedisHmgetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisHmgetTerminalCommand parseCommand(String line, String[] words) {
        RedisHmgetTerminalCommand command = new RedisHmgetTerminalCommand();
        command.key(words[1]);
        command.fields(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHmgetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> values = terminal.client().hmget(null, command.key(), command.fields());
            result.setResult(RedisTerminalUtil.formatOut(values));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HMGET";
    }

    @Override
    public String commandArg() {
        return "key field [field ...]";
    }

    @Override
    public String commandDesc() {
        return "获取hash多个值";
    }

    @Override
    public boolean commandDeprecated() {
        return true;
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
