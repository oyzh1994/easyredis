package cn.oyzh.easyredis.shell.handler.list;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.list.RedisLpushxTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisLpushxTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisLpushxTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisLpushxTerminalCommand parseCommand(String line, String[] words) {
        RedisLpushxTerminalCommand command = new RedisLpushxTerminalCommand();
        command.key(words[1]);
        command.values(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisLpushxTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().lpushx(null, command.key(), command.values());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "LPUSHX";
    }

    @Override
    public String commandArg() {
        return "key value [value ...]";
    }

    @Override
    public String commandDesc() {
        return "从左侧推送数据到list(list需要存在)";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.LIST;
    }
}
