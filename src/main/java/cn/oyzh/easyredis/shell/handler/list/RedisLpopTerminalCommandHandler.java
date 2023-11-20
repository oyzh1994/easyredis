package cn.oyzh.easyredis.shell.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.list.RedisLpopTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisLpopTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLpopTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisLpopTerminalCommand parseCommand(String line, String[] words) {
        RedisLpopTerminalCommand command = new RedisLpopTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Integer.parseInt(words[2]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLpopTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() != null) {
                List<String> lpop = terminal.client().lpop(null, command.key(), command.count());
                result.setResult(RedisShellUtil.formatOut(lpop));
            } else {
                String lpop = terminal.client().lpop(null, command.key());
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
        return "LPOP";
    }

    @Override
    public String commandArg() {
        return "key [count]";
    }

    @Override
    public String commandDesc() {
        return "从list头部弹出值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
