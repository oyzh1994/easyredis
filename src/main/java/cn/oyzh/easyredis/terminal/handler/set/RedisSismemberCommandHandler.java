package cn.oyzh.easyredis.terminal.handler.set;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.set.RedisSismemberTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSismemberCommandHandler extends RedisKeyTerminalCommandHandler<RedisSismemberTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisSismemberTerminalCommand parseCommand(String line, String[] words) {
        RedisSismemberTerminalCommand command = new RedisSismemberTerminalCommand();
        command.key(words[1]);
        command.member(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSismemberTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            boolean sismember = terminal.client().sismember(null, command.key(), command.member());
            result.setResult(RedisTerminalUtil.formatOut(sismember));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SISMEMBER";
    }

    @Override
    public String commandArg() {
        return "key member";
    }

    @Override
    public String commandDesc() {
        return "是否set成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
