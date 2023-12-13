package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisObjectRefcountTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisKeyTerminalCommand parseCommand(String line, String[] words) throws RuntimeException {
        RedisKeyTerminalCommand command = new RedisKeyTerminalCommand();
        command.key(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Long objectIdletime = terminal.client().objectRefcount(null, command.key());
            result.setResult(RedisShellUtil.formatOut(objectIdletime));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "OBJECT";
    }

    @Override
    public String commandSubName() {
        return "REFCOUNT";
    }

    @Override
    public String commandDesc() {
        return "获取键引用数量";
    }

}
