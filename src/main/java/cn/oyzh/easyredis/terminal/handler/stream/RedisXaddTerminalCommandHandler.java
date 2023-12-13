package cn.oyzh.easyredis.terminal.handler.stream;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.stream.RedisXaddTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.StreamEntryID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisXaddTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisXaddTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 4 && (words.length - 3) % 2 == 0;
    }

    @Override
    protected RedisXaddTerminalCommand parseCommand(String line, String[] words) {
        RedisXaddTerminalCommand command = new RedisXaddTerminalCommand();
        command.key(words[1]);
        command.id(words[2]);
        Map<String, String> hash = new HashMap<>();
        for (int i = 3; i < words.length; i += 2) {
            String field = words[i];
            String value = words[i + 1];
            hash.put(field, value);
        }
        command.hash(hash);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisXaddTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            StreamEntryID id = terminal.client().xadd(null, command.key(), command.hash(), command.addParams());
            result.setResult(RedisTerminalUtil.formatOut(id));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "XADD";
    }

    @Override
    public String commandArg() {
        return "key id filed value [field value...]";
    }

    @Override
    public String commandDesc() {
        return "添加stream消息";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }
}
