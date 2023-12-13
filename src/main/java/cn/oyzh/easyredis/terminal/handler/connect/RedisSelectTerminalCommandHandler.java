package cn.oyzh.easyredis.terminal.handler.connect;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.connect.RedisSelectTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisSelectTerminalCommandHandler extends RedisTerminalCommandHandler<RedisSelectTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    protected RedisSelectTerminalCommand parseCommand(String line, String[] words) {
        RedisSelectTerminalCommand command = new RedisSelectTerminalCommand();
        command.dbIndex(Integer.parseInt(words[1]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSelectTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().select(command.dbIndex());
            result.setResult(RedisShellUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SELECT";
    }

    @Override
    public String commandArg() {
        return "db";
    }

    @Override
    public String commandDesc() {
        return "切换数据库";
    }

}
