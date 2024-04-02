package cn.oyzh.easyredis.terminal.handler.stream;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.stream.RedisXdelTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisXdelTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisXdelTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisXdelTerminalCommand parseCommand(String line, String[] words) {
        RedisXdelTerminalCommand command = new RedisXdelTerminalCommand();
        command.key(words[1]);
        command.idsOfString(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisXdelTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().xdel(null, command.key(), command.ids());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "XDEL";
    }

    @Override
    public String commandArg() {
        return "key id [id...]";
    }

    @Override
    public String commandDesc() {
        return "删除stream消息";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }
}
