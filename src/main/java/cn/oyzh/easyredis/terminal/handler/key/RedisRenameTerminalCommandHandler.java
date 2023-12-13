package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.key.RedisRenameTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisRenameTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisRenameTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisRenameTerminalCommand parseCommand(String line, String[] words) {
        RedisRenameTerminalCommand command = new RedisRenameTerminalCommand();
        command.key(words[1]);
        command.newKey(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisRenameTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().rename(null, command.key(), command.newKey());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "RENAME";
    }

    @Override
    public String commandArg() {
        return "key newkey";
    }

    @Override
    public String commandDesc() {
        return "重命名键";
    }

}
