package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.hash.RedisHgetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHgetCommandHandler extends RedisKeyTerminalCommandHandler<RedisHgetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisHgetTerminalCommand parseCommand(String line, String[] words) {
        RedisHgetTerminalCommand command = new RedisHgetTerminalCommand();
        command.key(words[1]);
        command.field(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHgetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String value = terminal.client().hget(null, command.key(), command.field());
            result.setResult(RedisShellUtil.formatOut(value));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HGET";
    }

    @Override
    public String commandArg() {
        return "key field";
    }

    @Override
    public String commandDesc() {
        return "获取hash值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
