package cn.oyzh.easyredis.terminal.handler.zset;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZdiffTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZdiffTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZdiffTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisZdiffTerminalCommand parseCommand(String line, String[] words) {
        RedisZdiffTerminalCommand command = new RedisZdiffTerminalCommand();
        command.numkeys(Integer.parseInt(words[1]));
        if (StrUtil.equalsIgnoreCase("WITHSCORES", ArrUtil.last(words))) {
            command.keys(ArrUtil.sub(words, 2, words.length - 1));
            command.withScores(true);
        } else {
            command.keys(ArrUtil.sub(words, 2));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZdiffTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            if (command.withScores()) {
                Set<Tuple> zdiffWithScores = terminal.client().zdiffWithScores(null, command.keys());
                List<Object> list = new ArrayList<>();
                for (Tuple tuple : zdiffWithScores) {
                    list.add(tuple.getElement());
                    list.add(tuple.getScore());
                }
                result.setResult(RedisShellUtil.formatOut(list));
            } else {
                Set<String> zdiff = terminal.client().zdiff(null, command.keys());
                result.setResult(RedisShellUtil.formatOut(zdiff));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZDIFF";
    }

    @Override
    public String commandArg() {
        return "numkeys key [key ...] [WITHSCORES]";
    }

    @Override
    public String commandDesc() {
        return "获取多个zset的差集";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
