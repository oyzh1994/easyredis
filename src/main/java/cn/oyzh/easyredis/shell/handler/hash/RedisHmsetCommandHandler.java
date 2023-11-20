package cn.oyzh.easyredis.shell.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hash.RedisHmsetTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHmsetCommandHandler extends RedisKeyTerminalCommandHandler<RedisHmsetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 3 && words.length % 2 == 0;
    }

    @Override
    protected RedisHmsetTerminalCommand parseCommand(String line, String[] words) {
        RedisHmsetTerminalCommand command = new RedisHmsetTerminalCommand();
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
    public TerminalExecuteResult execute(RedisHmsetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().hmset(null, command.key(), command.hash());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HMSET";
    }

    @Override
    public String commandArg() {
        return "key field value [field value ...]";
    }

    @Override
    public String commandDesc() {
        return "设置hash多个字段";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
