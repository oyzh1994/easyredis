package cn.oyzh.easyredis.terminal.handler.string;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.string.RedisMsetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisMsetTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisMsetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2 && (words.length - 1) % 2 == 0;
    }

    @Override
    protected RedisMsetTerminalCommand parseCommand(String line, String[] words) {
        RedisMsetTerminalCommand command = new RedisMsetTerminalCommand();
        command.keyValues(ArrayUtil.sub(words, 1, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisMsetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().mset(null, command.keyValues());
            result.setResult(RedisTerminalUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "MSET";
    }

    @Override
    public String commandArg() {
        return "key value [key value ...]";
    }

    @Override
    public String commandDesc() {
        return "设置多个string值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
