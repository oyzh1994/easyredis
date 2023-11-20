package cn.oyzh.easyredis.shell.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.zset.RedisZrankTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZrankTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZrankTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }


    @Override
    protected RedisZrankTerminalCommand parseCommand(String line, String[] words) {
        RedisZrankTerminalCommand command = new RedisZrankTerminalCommand();
        command.key(words[1]);
        command.member(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZrankTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Long zrank = terminal.client().zrank(null, command.key(), command.member());
            result.setResult(RedisShellUtil.formatOut(zrank));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZRANK";
    }

    @Override
    public String commandArg() {
        return "key member";
    }

    @Override
    public String commandDesc() {
        return "获取zset成员排名";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
