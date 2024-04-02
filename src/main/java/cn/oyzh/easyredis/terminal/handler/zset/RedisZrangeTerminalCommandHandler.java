package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.zset.RedisZrangeTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZrangeTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZrangeTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4;
    }


    @Override
    protected RedisZrangeTerminalCommand parseCommand(String line, String[] words) {
        RedisZrangeTerminalCommand command = new RedisZrangeTerminalCommand();
        command.key(words[1]);
        command.start(Integer.parseInt(words[2]));
        command.end(Integer.parseInt(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZrangeTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> members = terminal.client().zrange(null, command.key(), command.start(), command.end());
            result.setResult(RedisTerminalUtil.formatOut(members));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZRANGE";
    }

    @Override
    public String commandArg() {
        return "key start end";
    }

    @Override
    public String commandDesc() {
        return "获取zset指定区间成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
