package cn.oyzh.easyredis.terminal.handler.set;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSdiffstoreTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSdiffstoreTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSdiffstoreTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisSdiffstoreTerminalCommand parseCommand(String line, String[] words) {
        RedisSdiffstoreTerminalCommand command = new RedisSdiffstoreTerminalCommand();
        command.key(words[2]);
        command.keys(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSdiffstoreTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long sdiffstore = terminal.client().sdiffstore(null, command.key(), command.keys());
            result.setResult(RedisShellUtil.formatOut(sdiffstore));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SDIFFSTORE";
    }

    @Override
    public String commandArg() {
        return "destkey key [key...]";
    }

    @Override
    public String commandDesc() {
        return "获取多个set的差集，并保存到目标set";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
