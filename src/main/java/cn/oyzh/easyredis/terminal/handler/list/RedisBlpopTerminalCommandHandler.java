package cn.oyzh.easyredis.terminal.handler.list;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.list.RedisBlpopTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisBlpopTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisBlpopTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisBlpopTerminalCommand parseCommand(String line, String[] words) {
        RedisBlpopTerminalCommand command = new RedisBlpopTerminalCommand();
        command.timeout(Integer.parseInt(ArrUtil.last(words)));
        command.keys(ArrUtil.sub(words, 1, words.length - 1));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisBlpopTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> blpop = terminal.client().blpop(null, command.timeout(), command.keys());
            result.setResult(RedisShellUtil.formatOut(blpop));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "BLPOP";
    }

    @Override
    public String commandArg() {
        return "timeout key [key...]";
    }

    @Override
    public String commandDesc() {
        return "从list头部弹出值，直到超时";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
