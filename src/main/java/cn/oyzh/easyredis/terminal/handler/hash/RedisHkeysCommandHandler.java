package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
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
public class RedisHkeysCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Set<String> hkeys = terminal.client().hkeys(null, command.key());
            result.setResult(RedisShellUtil.formatOut(hkeys));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HKEYS";
    }

    @Override
    public String commandDesc() {
        return "获取hash字段列表";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
