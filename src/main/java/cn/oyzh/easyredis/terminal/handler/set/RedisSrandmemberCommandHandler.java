package cn.oyzh.easyredis.terminal.handler.set;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.set.RedisSrandmemberTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisSrandmemberCommandHandler extends RedisKeyTerminalCommandHandler<RedisSrandmemberTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisSrandmemberTerminalCommand parseCommand(String line, String[] words) {
        RedisSrandmemberTerminalCommand command = new RedisSrandmemberTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.count(Integer.parseInt(words[2]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisSrandmemberTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.count() != null) {
                List<String> smembers = terminal.client().srandmember(null, command.key(), command.count());
                result.setResult(RedisTerminalUtil.formatOut(smembers));
            } else {
                String srandmember = terminal.client().srandmember(null, command.key());
                result.setResult(RedisTerminalUtil.formatOut(srandmember));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SRANDMEMBER";
    }

    @Override
    public String commandDesc() {
        return "随机返回set成员";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.SET;
    }
}
