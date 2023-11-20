package cn.oyzh.easyredis.shell.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hash.RedisHexistsTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHstrlenCommandHandler extends RedisKeyTerminalCommandHandler<RedisHexistsTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisHexistsTerminalCommand parseCommand(String line, String[] words) {
        RedisHexistsTerminalCommand command = new RedisHexistsTerminalCommand();
        command.key(words[1]);
        command.field(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHexistsTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().hstrlen(null, command.key(), command.field());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HSTRLEN";
    }

    @Override
    public String commandArg() {
        return "key field";
    }

    @Override
    public String commandDesc() {
        return "获取hash字段值长度";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
