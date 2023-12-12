package cn.oyzh.easyredis.shell.handler.hyperloglog;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hyperloglog.RedisPfcountTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisPfcountTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisPfcountTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisPfcountTerminalCommand parseCommand(String line, String[] words) {
        RedisPfcountTerminalCommand command = new RedisPfcountTerminalCommand();
        command.keys(ArrayUtil.sub(words, 1, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisPfcountTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().pfcount(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PFCOUNT";
    }

    @Override
    public String commandArg() {
        return "key [key...]";
    }

    @Override
    public String commandDesc() {
        return "获取hyperloglog的统计值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
