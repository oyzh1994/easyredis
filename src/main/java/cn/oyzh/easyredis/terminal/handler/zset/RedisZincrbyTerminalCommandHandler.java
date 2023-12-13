package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZincrbyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisZincrbyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZincrbyTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }

    @Override
    protected RedisZincrbyTerminalCommand parseCommand(String line, String[] words) {
        RedisZincrbyTerminalCommand command = new RedisZincrbyTerminalCommand();
        command.key(words[1]);
        command.increment(Double.parseDouble(words[2]));
        command.member(words[3]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZincrbyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            double zincrby = terminal.client().zincrby(null, command.key(), command.increment(), command.member());
            result.setResult(RedisShellUtil.formatOut(zincrby));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZINCRBY";
    }

    @Override
    public String commandArg() {
        return "key increment member";
    }

    @Override
    public String commandDesc() {
        return "添加zset成员分数";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
