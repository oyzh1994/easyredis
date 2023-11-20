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
public class RedisExistsTerminalCommandHandler extends RedisNKeysTerminalCommandHandler<RedisNKeysTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisNKeysTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long exists = terminal.client().exists(null, command.keys());
            result.setResult(RedisShellUtil.formatOut(exists));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "EXISTS";
    }

    @Override
    public String commandDesc() {
        return "键是否存在";
    }

}
