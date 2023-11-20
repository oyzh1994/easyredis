package cn.oyzh.easyredis.shell.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.zset.RedisZaddTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZaddTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZaddTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 3 && words.length % 2 == 0;
    }

    @Override
    protected RedisZaddTerminalCommand parseCommand(String line, String[] words) {
        RedisZaddTerminalCommand command = new RedisZaddTerminalCommand();
        command.key(words[1]);
        Map<String, Double> members = new HashMap<>();
        for (int i = 2; i < words.length; i += 2) {
            double score = Double.parseDouble(words[i]);
            String member = words[i + 1];
            members.put(member, score);
        }
        command.members(members);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZaddTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().zadd(null, command.key(), command.members());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZADD";
    }

    @Override
    public String commandArg() {
        return "key score member [score member...]";
    }

    @Override
    public String commandDesc() {
        return "添加zset成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
