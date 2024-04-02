package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.hash.RedisHsetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHsetCommandHandler extends RedisKeyTerminalCommandHandler<RedisHsetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 3 && words.length % 2 == 0;
    }

    @Override
    protected RedisHsetTerminalCommand parseCommand(String line, String[] words) {
        RedisHsetTerminalCommand command = new RedisHsetTerminalCommand();
        command.key(words[1]);
        Map<String, String> hash = new HashMap<>();
        for (int i = 2; i < words.length; i += 2) {
            String field = words[i];
            String value = words[i + 1];
            hash.put(field, value);
        }
        command.hash(hash);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHsetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().hset(null, command.key(), command.hash());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HSET";
    }

    @Override
    public String commandArg() {
        return "key field value [field value...]";
    }

    @Override
    public String commandDesc() {
        return "设置hash内容";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
