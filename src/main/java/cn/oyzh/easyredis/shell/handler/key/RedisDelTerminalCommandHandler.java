package cn.oyzh.easyredis.shell.handler.key;

import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.RedisNKeysTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisNKeysTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisDelTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<RedisNKeysTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisNKeysTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().del(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "DEL";
    }

    @Override
    public String commandDesc() {
        return "删除键";
    }

}
