package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZrandmemberTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZrandmemberCommandHandler extends RedisKeyTerminalCommandHandler<RedisZrandmemberTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisZrandmemberTerminalCommand parseCommand(String line, String[] words) {
        RedisZrandmemberTerminalCommand command = new RedisZrandmemberTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Integer.parseInt(words[2]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZrandmemberTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() != null) {
                List<String> smembers = terminal.client().zrandmember(null, command.key(), command.count());
                result.setResult(RedisShellUtil.formatOut(smembers));
            } else {
                String srandmember = terminal.client().zrandmember(null, command.key());
                result.setResult(RedisShellUtil.formatOut(srandmember));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZRANDMEMBER";
    }

    @Override
    public String commandArg() {
        return "key [count]";
    }

    @Override
    public String commandDesc() {
        return "随机返回zset成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
