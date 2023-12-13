package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisSetnxTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSetnxTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSetnxTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisSetnxTerminalCommand parseCommand(String line, String[] words) {
        RedisSetnxTerminalCommand command = new RedisSetnxTerminalCommand();
        command.key(words[1]);
        command.value(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSetnxTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().setnx(null, command.key(), command.value());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SETNX";
    }

    @Override
    public String commandArg() {
        return "key value";
    }

    @Override
    public String commandDesc() {
        return "设置string值，仅键不存在时";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
