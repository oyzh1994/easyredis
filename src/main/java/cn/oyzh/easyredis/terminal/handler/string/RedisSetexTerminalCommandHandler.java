package cn.oyzh.easyredis.terminal.handler.string;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.string.RedisSetexTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisSetexTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisSetexTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisSetexTerminalCommand parseCommand(String line, String[] words) {
        RedisSetexTerminalCommand command = new RedisSetexTerminalCommand();
        command.key(words[1]);
        command.seconds(Long.parseLong(words[2]));
        command.value(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSetexTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().setex(null, command.key(), command.seconds(), command.value());
            result.setResult(RedisTerminalUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SETEX";
    }

    @Override
    public String commandArg() {
        return "key seconds value";
    }

    @Override
    public String commandDesc() {
        return "设置string值并更新ttl";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STRING;
    }
}
