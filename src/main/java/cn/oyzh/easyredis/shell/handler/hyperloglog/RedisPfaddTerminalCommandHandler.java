package cn.oyzh.easyredis.shell.handler.hyperloglog;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hyperloglog.RedisPfaddTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisPfaddTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisPfaddTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisPfaddTerminalCommand parseCommand(String line, String[] words) {
        RedisPfaddTerminalCommand command = new RedisPfaddTerminalCommand();
        command.key(words[1]);
        command.elements(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisPfaddTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().pfadd(null, command.key(), command.elements());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PFADD";
    }

    @Override
    public String commandArg() {
        return "key value [value...]";
    }

    @Override
    public String commandDesc() {
        return "添加hyperloglog统计值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HYPERLOGLOG;
    }
}
