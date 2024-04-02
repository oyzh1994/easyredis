package cn.oyzh.easyredis.terminal.handler.bit;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.bit.RedisBitposCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisBitposTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisBitposCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3 || words.length == 4 || words.length == 5;
    }

    @Override
    protected RedisBitposCommand parseCommand(String line, String[] words) {
        RedisBitposCommand command = new RedisBitposCommand();
        command.key(words[1]);
        command.valueOfString(words[2]);
        if (words.length >= 4) {
            command.startOfString(words[3]);
        }
        if (words.length == 5) {
            command.endOfString(words[3]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisBitposCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long bitpos = terminal.client().bitpos(null, command.key(), command.value(), command.params());
            result.setResult(RedisTerminalUtil.formatOut(bitpos));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "BITPOS";
    }

    @Override
    public String commandArg() {
        return "key bit [start [end [BYTE | BIT]]]";
    }

    @Override
    public String commandDesc() {
        return "获取bit值首次出现的位置";
    }
}
