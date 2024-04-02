package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.key.RedisExpireTerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisPexpireAtTerminalCommandHandler extends RedisExpireTerminalCommandHandler {

    @Override
    public TerminalExecuteResult execute(RedisExpireTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().pexpireAt(null, command.key(), command.time(), command.option());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PEXPIREAT";
    }

    @Override
    public String commandArg() {
        return "key millisecondsTimestamp [condition]";
    }

    @Override
    public String commandDesc() {
        return "设置到期期时间，以毫秒为单位";
    }

}
