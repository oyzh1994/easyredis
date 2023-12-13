package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.list.RedisLrangeTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisLrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLrangeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisLrangeTerminalCommand parseCommand(String line, String[] words) {
        RedisLrangeTerminalCommand command = new RedisLrangeTerminalCommand();
        command.key(words[1]);
        command.start(Integer.parseInt(words[2]));
        command.end(Integer.parseInt(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLrangeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> values = terminal.client().lrange(null, command.key(), command.start(), command.end());
            result.setResult(RedisShellUtil.formatOut(values));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LRANGE";
    }

    @Override
    public String commandArg() {
        return "key start end";
    }

    @Override
    public String commandDesc() {
        return "获取list指定区间内的值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
