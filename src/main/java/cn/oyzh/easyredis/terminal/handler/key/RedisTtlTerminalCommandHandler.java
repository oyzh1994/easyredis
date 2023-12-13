package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisTtlTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long ttl = terminal.client().ttl(null, command.key());
            result.setResult(RedisShellUtil.formatOut(ttl));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "TTL";
    }

    @Override
    public String commandDesc() {
        return "获取键剩余存活时间";
    }
}
