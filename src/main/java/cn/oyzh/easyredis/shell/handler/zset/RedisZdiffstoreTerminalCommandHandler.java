package cn.oyzh.easyredis.shell.handler.zset;

import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.shell.RedisShellUtil;
import cn.oyzh.easyredis.shell.RedisTerminalTextArea;
import cn.oyzh.easyredis.shell.command.zset.RedisZdiffstoreTerminalCommand;
import cn.oyzh.easyredis.shell.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZdiffstoreTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZdiffstoreTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 3;
    }

    @Override
    protected RedisZdiffstoreTerminalCommand parseCommand(String line, String[] words) {
        RedisZdiffstoreTerminalCommand command = new RedisZdiffstoreTerminalCommand();
        command.key(words[2]);
        command.numkeys(Integer.parseInt(words[3]));
        command.keys(ArrUtil.sub(words, 3));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZdiffstoreTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long sdiffstore = terminal.client().zdiffStore(null, command.key(), command.keys());
            result.setResult(RedisShellUtil.formatOut(sdiffstore));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZDIFFSTORE";
    }

    @Override
    public String commandArg() {
        return "destination numkeys key [key ...]";
    }

    @Override
    public String commandDesc() {
        return "获取多个zset的差集，并保存到目标zset";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
