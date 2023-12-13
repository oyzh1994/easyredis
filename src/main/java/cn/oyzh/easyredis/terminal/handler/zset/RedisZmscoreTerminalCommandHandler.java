package cn.oyzh.easyredis.terminal.handler.zset;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZmscoreTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZmscoreTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZmscoreTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisZmscoreTerminalCommand parseCommand(String line, String[] words) {
        RedisZmscoreTerminalCommand command = new RedisZmscoreTerminalCommand();
        command.key(words[1]);
        command.members(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZmscoreTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<Double> zmscore = terminal.client().zmscore(null, command.key(), command.members());
            result.setResult(RedisTerminalUtil.formatOut(zmscore));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZMSCORE";
    }

    @Override
    public String commandArg() {
        return "key member [member...]";
    }

    @Override
    public String commandDesc() {
        return "获取zset多个成员分数";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
