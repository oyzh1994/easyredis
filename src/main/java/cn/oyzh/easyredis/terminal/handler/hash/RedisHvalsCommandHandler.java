package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisHvalsCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> hvals = terminal.client().hvals(null, command.key());
            result.setResult(RedisShellUtil.formatOut(hvals));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HVALS";
    }

    @Override
    public String commandDesc() {
        return "获取hash值列表";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
