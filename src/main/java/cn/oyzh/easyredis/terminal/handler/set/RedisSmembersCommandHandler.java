package cn.oyzh.easyredis.terminal.handler.set;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSmembersCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Set<String> smembers = terminal.client().smembers(null, command.key());
            result.setResult(RedisTerminalUtil.formatOut(smembers));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SMEMBERS";
    }

    @Override
    public String commandDesc() {
        return "获取set成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
