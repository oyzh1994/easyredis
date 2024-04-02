package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.server.RedisClientSetnameTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisClientSetnameTerminalCommandHandler extends RedisTerminalCommandHandler<RedisClientSetnameTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisClientSetnameTerminalCommand parseCommand(String line, String[] words) {
        RedisClientSetnameTerminalCommand command = new RedisClientSetnameTerminalCommand();
        command.name(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisClientSetnameTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().clientSetname(command.name());
            result.setResult(RedisTerminalUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "CLIENT";
    }

    @Override
    public String commandSubName() {
        return "SETNAME";
    }

    @Override
    public String commandArg() {
        return "name";
    }

    @Override
    public String commandDesc() {
        return "设置连接名称";
    }
}
