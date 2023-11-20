package cn.oyzh.easyredis.shell.handler.string;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.string.RedisMgetTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisMgetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisMgetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 1;
    }

    @Override
    protected RedisMgetTerminalCommand parseCommand(String line, String[] words) {
        RedisMgetTerminalCommand command = new RedisMgetTerminalCommand();
        command.keys(ArrayUtil.sub(words, 1, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisMgetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> values = terminal.client().mget(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(values));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "MGET";
    }

    @Override
    public String commandArg() {
        return "key [key...]";
    }

    @Override
    public String commandDesc() {
        return "获取多个string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
