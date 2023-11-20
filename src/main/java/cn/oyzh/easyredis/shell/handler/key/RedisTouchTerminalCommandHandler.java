package cn.oyzh.easyredis.shell.handler.key;

import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.RedisNKeysTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisNKeysTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisTouchTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<RedisNKeysTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisNKeysTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().touch(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "TOUCH";
    }

    @Override
    public String commandArg() {
        return "key [key ...]";
    }

    @Override
    public String commandDesc() {
        return "修改键的最后访问时间";
    }

}
