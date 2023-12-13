package cn.oyzh.easyredis.terminal.handler.set;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSunionstoreTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSunionstoreTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSunionstoreTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisSunionstoreTerminalCommand parseCommand(String line, String[] words) {
        RedisSunionstoreTerminalCommand command = new RedisSunionstoreTerminalCommand();
        command.key(words[2]);
        command.keys(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSunionstoreTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long sunionstore = terminal.client().sunionstore(null, command.key(), command.keys());
            result.setResult(RedisShellUtil.formatOut(sunionstore));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SUNIONSTORE";
    }

    @Override
    public String commandArg() {
        return "destkey key [key...]";
    }

    @Override
    public String commandDesc() {
        return "获取多个set的并集，并保存到目标set";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
