package cn.oyzh.easyredis.terminal.handler.bit;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.bit.RedisBitcountCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisBitcountTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisBitcountCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3 || words.length == 4 || words.length == 5;
    }

    @Override
    protected RedisBitcountCommand parseCommand(String line, String[] words) {
        RedisBitcountCommand command = new RedisBitcountCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.optionOfString(words[2]);
        } else if (words.length == 4) {
            command.start(Long.valueOf(words[2]));
            command.end(Long.valueOf(words[3]));
        } else if (words.length == 5) {
            command.start(Long.valueOf(words[2]));
            command.end(Long.valueOf(words[3]));
            command.optionOfString(words[4]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisBitcountCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long bitcount = terminal.client().bitcount(null, command.key(), command.start(), command.end(), command.option());
            result.setResult(RedisTerminalUtil.formatOut(bitcount));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "BITCOUNT";
    }

    @Override
    public String commandArg() {
        return "key [start end [BYTE | BIT]]";
    }

    @Override
    public String commandDesc() {
        return "统计bit值";
    }
}
