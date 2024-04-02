package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.key.RedisExpireTerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisPexpireTerminalCommandHandler extends RedisExpireTerminalCommandHandler {

    @Override
    public TerminalExecuteResult execute(RedisExpireTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().pexpire(null, command.key(), command.time(), command.option());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PEXPIRE";
    }

    @Override
    public String commandArg() {
        return "key milliseconds [condition]";
    }

    @Override
    public String commandDesc() {
        return "设置过期时间，以毫秒为单位";
    }

}
