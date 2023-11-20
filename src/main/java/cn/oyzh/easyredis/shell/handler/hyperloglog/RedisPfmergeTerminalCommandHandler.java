package cn.oyzh.easyredis.shell.handler.hyperloglog;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hyperloglog.RedisPfmergeTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisPfmergeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisPfmergeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisPfmergeTerminalCommand parseCommand(String line, String[] words) {
        RedisPfmergeTerminalCommand command = new RedisPfmergeTerminalCommand();
        command.key(words[1]);
        command.sourceKeys(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisPfmergeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().pfmerge(null, command.key(), command.sourceKeys());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PFMERGE";
    }

    @Override
    public String commandArg() {
        return "destkey sourcekey [sourcekey...]";
    }

    @Override
    public String commandDesc() {
        return "合并多个hyperloglog";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HYPERLOGLOG;
    }
}
