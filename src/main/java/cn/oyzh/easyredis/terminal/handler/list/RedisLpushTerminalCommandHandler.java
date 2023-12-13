package cn.oyzh.easyredis.terminal.handler.list;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.list.RedisLpushTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisLpushTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLpushTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisLpushTerminalCommand parseCommand(String line, String[] words) {
        RedisLpushTerminalCommand command = new RedisLpushTerminalCommand();
        command.key(words[1]);
        command.values(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLpushTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().lpush(null, command.key(), command.values());
            result.setResult(RedisTerminalUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LPUSH";
    }

    @Override
    public String commandArg() {
        return "key value [value ...]";
    }

    @Override
    public String commandDesc() {
        return "从左侧推送数据到list";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
