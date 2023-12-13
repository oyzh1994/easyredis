package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.hash.RedisHexistsTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHexistsCommandHandler extends RedisKeyTerminalCommandHandler<RedisHexistsTerminalCommand> {

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
            boolean hexists = terminal.client().hexists(null, command.key(), command.field());
            result.setResult(RedisShellUtil.formatOut(hexists));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HEXISTS";
    }

    @Override
    public String commandArg() {
        return "key field";
    }

    @Override
    public String commandDesc() {
        return "判断hash字段是否存在";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
