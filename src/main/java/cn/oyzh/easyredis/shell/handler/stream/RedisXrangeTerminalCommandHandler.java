package cn.oyzh.easyredis.shell.handler.stream;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.stream.RedisXrangeTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisXrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisXrangeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3 || words.length == 4;
    }

    @Override
    protected RedisXrangeTerminalCommand parseCommand(String line, String[] words) {
        RedisXrangeTerminalCommand command = new RedisXrangeTerminalCommand();
        command.key(words[1]);
        command.startOfString(words[2]);
        command.endOfString(words[3]);
        if (words.length == 5) {
            command.count(Integer.valueOf(words[4]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisXrangeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<StreamEntry> entries = terminal.client().xrange(null, command.key(), command.start(), command.end(), command.count());
            result.setResult(RedisShellUtil.formatOutStream(entries));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "XRANGE";
    }

    @Override
    public String commandArg() {
        return "key start end [count]";
    }

    @Override
    public String commandDesc() {
        return "获取stream区间内的消息";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }
}
