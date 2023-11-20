package cn.oyzh.easyredis.shell.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.list.RedisLremTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisLremTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLremTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisLremTerminalCommand parseCommand(String line, String[] words) {
        RedisLremTerminalCommand command = new RedisLremTerminalCommand();
        command.key(words[1]);
        command.count(Long.parseLong(words[2]));
        command.value(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLremTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().lrem(null, command.key(), command.count(), command.value());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LREM";
    }

    @Override
    public String commandArg() {
        return "key count value";
    }

    @Override
    public String commandDesc() {
        return "删除list指定值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
