package cn.oyzh.easyredis.terminal.handler.set;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSpopTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSpopTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSpopTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisSpopTerminalCommand parseCommand(String line, String[] words) {
        RedisSpopTerminalCommand command = new RedisSpopTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Long.parseLong(words[2]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSpopTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() != null) {
                Set<String> spop = terminal.client().spop(null, command.key(), command.count());
                result.setResult(RedisTerminalUtil.formatOut(spop));
            } else {
                String spop = terminal.client().spop(null, command.key());
                result.setResult(RedisTerminalUtil.formatOut(spop));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SPOP";
    }

    @Override
    public String commandArg() {
        return "key [count]";
    }

    @Override
    public String commandDesc() {
        return "从set头部弹出成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
