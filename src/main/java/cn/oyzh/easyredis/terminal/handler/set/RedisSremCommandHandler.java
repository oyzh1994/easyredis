package cn.oyzh.easyredis.terminal.handler.set;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSremTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSremCommandHandler extends RedisKeyTerminalCommandHandler<RedisSremTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisSremTerminalCommand parseCommand(String line, String[] words) {
        RedisSremTerminalCommand command = new RedisSremTerminalCommand();
        command.key(words[1]);
        command.members(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSremTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().srem(null, command.key(), command.members());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SREM";
    }

    @Override
    public String commandArg() {
        return "key member [member...]";
    }

    @Override
    public String commandDesc() {
        return "删除set成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
