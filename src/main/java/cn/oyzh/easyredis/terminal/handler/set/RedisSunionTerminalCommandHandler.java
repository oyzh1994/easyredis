package cn.oyzh.easyredis.terminal.handler.set;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSunionTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSunionTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSunionTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 1;
    }

    @Override
    protected RedisSunionTerminalCommand parseCommand(String line, String[] words) {
        RedisSunionTerminalCommand command = new RedisSunionTerminalCommand();
        command.keys(ArrayUtil.sub(words, 1, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSunionTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Set<String> sunion = terminal.client().sunion(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(sunion));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SUNION";
    }

    @Override
    public String commandArg() {
        return "key [key...]";
    }

    @Override
    public String commandDesc() {
        return "获取多个set的并集";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
