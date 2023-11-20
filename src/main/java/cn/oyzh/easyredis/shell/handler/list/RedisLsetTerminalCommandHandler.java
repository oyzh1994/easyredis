package cn.oyzh.easyredis.shell.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.list.RedisLsetTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisLsetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLsetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisLsetTerminalCommand parseCommand(String line, String[] words) {
        RedisLsetTerminalCommand command = new RedisLsetTerminalCommand();
        command.key(words[1]);
        command.index(Integer.parseInt(words[2]));
        command.value(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLsetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().lset(null, command.key(), command.index(), command.value());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LSET";
    }

    @Override
    public String commandArg() {
        return "key index value";
    }

    @Override
    public String commandDesc() {
        return "设置list指定索引的值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
