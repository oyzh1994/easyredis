package cn.oyzh.easyredis.terminal.handler.string;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.string.RedisMsetnxTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisMsetnxTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisMsetnxTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2 && (words.length - 1) % 2 == 0;
    }

    @Override
    protected RedisMsetnxTerminalCommand parseCommand(String line, String[] words) {
        RedisMsetnxTerminalCommand command = new RedisMsetnxTerminalCommand();
        command.keyValues(ArrayUtil.sub(words, 1, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisMsetnxTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().msetnx(null, command.keyValues());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "MSETNX";
    }

    @Override
    public String commandArg() {
        return "key value [key value ...]";
    }

    @Override
    public String commandDesc() {
        return "设置多个string值，仅键不存在时";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
