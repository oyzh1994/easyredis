package cn.oyzh.easyredis.shell.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.list.RedisLindexTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisLindexTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLindexTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisLindexTerminalCommand parseCommand(String line, String[] words) {
        RedisLindexTerminalCommand command = new RedisLindexTerminalCommand();
        command.key(words[1]);
        command.index(Long.parseLong(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLindexTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().lindex(null, command.key(), command.index());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LINDEX";
    }

    @Override
    public String commandArg() {
        return "key index";
    }

    @Override
    public String commandDesc() {
        return "获取list指定索引处的值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
