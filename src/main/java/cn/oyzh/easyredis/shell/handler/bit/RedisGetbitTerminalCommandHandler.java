package cn.oyzh.easyredis.shell.handler.bit;

import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.bit.RedisGetbitCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisGetbitTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGetbitCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisGetbitCommand parseCommand(String line, String[] words) {
        RedisGetbitCommand command = new RedisGetbitCommand();
        command.key(words[1]);
        command.offset(Long.parseLong(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGetbitCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            boolean getbit = terminal.client().getbit(null, command.key(), command.offset());
            result.setResult(RedisShellUtil.formatOut(getbit));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GETBIT";
    }

    @Override
    public String commandArg() {
        return "key offset";
    }

    @Override
    public String commandDesc() {
        return "获取bit值";
    }
}
