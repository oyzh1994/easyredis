package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.server.RedisConfigGetTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisConfigGetTerminalCommandHandler extends RedisTerminalCommandHandler<RedisConfigGetTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisConfigGetTerminalCommand parseCommand(String line, String[] words) {
        RedisConfigGetTerminalCommand command = new RedisConfigGetTerminalCommand();
        command.pattern(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisConfigGetTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Map<String, String> configs = terminal.client().configGet(command.pattern());
            result.setResult(RedisTerminalUtil.formatOut(configs));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "CONFIG";
    }

    @Override
    public String commandSubName() {
        return "GET";
    }

    @Override
    public String commandArg() {
        return "pattern";
    }

    @Override
    public String commandDesc() {
        return "获取配置";
    }
}
