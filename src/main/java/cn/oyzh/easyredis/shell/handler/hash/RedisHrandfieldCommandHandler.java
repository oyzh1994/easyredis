package cn.oyzh.easyredis.shell.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.hash.RedisHrandfieldTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHrandfieldCommandHandler extends RedisKeyTerminalCommandHandler<RedisHrandfieldTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3 || words.length == 4;
    }

    @Override
    protected RedisHrandfieldTerminalCommand parseCommand(String line, String[] words) {
        RedisHrandfieldTerminalCommand command = new RedisHrandfieldTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Integer.valueOf(words[2]));
        } else if (words.length == 4) {
            command.count(Integer.valueOf(words[2]));
            command.withValuesOfString(words[3]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisHrandfieldTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() == null) {
                String hrandfield = terminal.client().hrandfield(null, command.key());
                result.setResult(RedisShellUtil.formatOut(hrandfield));
            } else if (command.withValues() == null) {
                List<String> hrandfield = terminal.client().hrandfield(null, command.key(), command.count());
                result.setResult(RedisShellUtil.formatOut(hrandfield));
            } else {
                Map<String, String> hrandfield = terminal.client().hrandfieldWithValues(null, command.key(), command.count());
                result.setResult(RedisShellUtil.formatOut(hrandfield));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HRANDFIELD";
    }

    @Override
    public String commandArg() {
        return "key [count [WITHVALUES]]";
    }

    @Override
    public String commandDesc() {
        return "返回随机hash字段";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
