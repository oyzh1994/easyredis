package cn.oyzh.easyredis.terminal.handler.bit;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.bit.RedisSetbitCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSetbitTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSetbitCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisSetbitCommand parseCommand(String line, String[] words) {
        RedisSetbitCommand command = new RedisSetbitCommand();
        command.key(words[1]);
        command.offset(Long.parseLong(words[2]));
        command.valueOfString(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSetbitCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            boolean setbit = terminal.client().setbit(null, command.key(), command.offset(), command.value());
            result.setResult(RedisShellUtil.formatOut(setbit));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SETBIT";
    }

    @Override
    public String commandArg() {
        return "key offset value";
    }

    @Override
    public String commandDesc() {
        return "设置bit值";
    }
}
