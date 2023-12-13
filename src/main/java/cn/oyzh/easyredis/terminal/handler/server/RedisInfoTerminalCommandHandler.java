package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.server.RedisInfoTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisInfoTerminalCommandHandler extends RedisTerminalCommandHandler<RedisInfoTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 1 || words.length == 2;
    }

    @Override
    protected RedisInfoTerminalCommand parseCommand(String line, String[] words) {
        RedisInfoTerminalCommand command = new RedisInfoTerminalCommand();
        if (words.length == 2) {
            command.section(words[1]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisInfoTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String info = terminal.client().info(command.section());
            result.setResult(RedisShellUtil.formatOut(info));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "INFO";
    }

    @Override
    public String commandArg() {
        return "[section]";
    }

    @Override
    public String commandDesc() {
        return "服务信息";
    }
}
