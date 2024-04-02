package cn.oyzh.easyredis.terminal.handler.geo;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.geo.RedisGeohashTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisGeohashTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGeohashTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisGeohashTerminalCommand parseCommand(String line, String[] words) {
        RedisGeohashTerminalCommand command = new RedisGeohashTerminalCommand();
        command.key(words[1]);
        command.members(ArrayUtil.sub(words, 2, words.length));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGeohashTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            List<String> hash = terminal.client().geohash(null, command.key(), command.members());
            result.setResult(RedisTerminalUtil.formatOut(hash));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GEOHASH";
    }

    @Override
    public String commandArg() {
        return "key [member [member ...]]";
    }

    @Override
    public String commandDesc() {
        return "获取geo坐标hash值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
