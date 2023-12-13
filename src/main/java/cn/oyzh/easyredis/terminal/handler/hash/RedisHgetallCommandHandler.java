package cn.oyzh.easyredis.terminal.handler.hash;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.RedisKeyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisHgetallCommandHandler extends RedisKeyTerminalCommandHandler<RedisKeyTerminalCommand> {

    @Override
    public TerminalExecuteResult execute(RedisKeyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Map<String, String> hash = terminal.client().hgetAll(null, command.key());
            List<String> list = new ArrayList<>();
            for (String s : hash.keySet()) {
                list.add(s);
                list.add(hash.get(s));
            }
            result.setResult(RedisShellUtil.formatOut(list));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "HGETALL";
    }

    @Override
    public String commandArg() {
        return "key";
    }

    @Override
    public String commandDesc() {
        return "获取hash所有字段及值";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.HASH;
    }
}
