package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.key.RedisMoveTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisMoveTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisMoveTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3 ;
    }

    @Override
    protected RedisMoveTerminalCommand parseCommand(String line, String[] words) {
        RedisMoveTerminalCommand command = new RedisMoveTerminalCommand();
        command.key(words[1]);
        command.targetDBIndex(Integer.parseInt(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisMoveTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().move(command.key(), null, command.targetDBIndex());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "MOVE";
    }

    @Override
    public String commandArg() {
        return "key db";
    }

    @Override
    public String commandDesc() {
        return "移动键";
    }
}
