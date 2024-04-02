package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.list.RedisLinsertTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisLinsertTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLinsertTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 5;
    }

    @Override
    protected RedisLinsertTerminalCommand parseCommand(String line, String[] words) {
        RedisLinsertTerminalCommand command = new RedisLinsertTerminalCommand();
        command.key(words[1]);
        command.whereOfString(words[2]);
        command.pivot(words[3]);
        command.value(words[4]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLinsertTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().linsert(null, command.key(), command.where(), command.pivot(), command.value());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LINSERT";
    }

    @Override
    public String commandArg() {
        return "key BEFORE|AFTER pivot value";
    }

    @Override
    public String commandDesc() {
        return "往list指定值位置插入值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
