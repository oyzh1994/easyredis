package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.list.RedisLtrimTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisLtrimTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLtrimTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisLtrimTerminalCommand parseCommand(String line, String[] words) {
        RedisLtrimTerminalCommand command = new RedisLtrimTerminalCommand();
        command.key(words[1]);
        command.start(Long.parseLong(words[2]));
        command.stop(Long.parseLong(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLtrimTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            String msg = terminal.client().ltrim(null, command.key(), command.start(), command.stop());
            result.setResult(RedisTerminalUtil.formatOut(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LTRIM";
    }

    @Override
    public String commandArg() {
        return "key start stop";
    }

    @Override
    public String commandDesc() {
        return "剪切list";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
