package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZcountTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZcountTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZcountTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisZcountTerminalCommand parseCommand(String line, String[] words) {
        RedisZcountTerminalCommand command = new RedisZcountTerminalCommand();
        command.key(words[1]);
        command.min(Double.parseDouble(words[2]));
        command.max(Double.parseDouble(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZcountTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long zcount = terminal.client().zcount(null, command.key(), command.min(), command.max());
            result.setResult(RedisTerminalUtil.formatOut(zcount));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZCOUNT";
    }

    @Override
    public String commandArg() {
        return "key min max";
    }

    @Override
    public String commandDesc() {
        return "获取zset指定分数区间成员数量";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
