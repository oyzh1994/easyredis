package cn.oyzh.easyredis.shell.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hash.RedisHsetnxTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHsetnxCommandHandler extends RedisKeyTerminalCommandHandler<RedisHsetnxTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisHsetnxTerminalCommand parseCommand(String line, String[] words) {
        RedisHsetnxTerminalCommand command = new RedisHsetnxTerminalCommand();
        command.key(words[1]);
        command.field(words[2]);
        command.value(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHsetnxTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().hsetnx(null, command.key(), command.field(), command.value());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HSETNX";
    }

    @Override
    public String commandArg() {
        return "key field value";
    }

    @Override
    public String commandDesc() {
        return "设置hash内容，hash键需存在";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
