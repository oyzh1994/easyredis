package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.list.RedisRpopTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisRpopTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisRpopTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisRpopTerminalCommand parseCommand(String line, String[] words) {
        RedisRpopTerminalCommand command = new RedisRpopTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Integer.parseInt(words[2]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisRpopTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() != null) {
                List<String> lpop = terminal.client().rpop(null, command.key(), command.count());
                result.setResult(RedisShellUtil.formatOut(lpop));
            } else {
                String lpop = terminal.client().rpop(null, command.key());
                result.setResult(RedisShellUtil.formatOut(lpop));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "RPOP";
    }

    @Override
    public String commandArg() {
        return "key [count]";
    }

    @Override
    public String commandDesc() {
        return "从list尾部弹出值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
