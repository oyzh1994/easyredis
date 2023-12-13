package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.zset.RedisZscoreTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZscoreTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZscoreTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }


    @Override
    protected RedisZscoreTerminalCommand parseCommand(String line, String[] words) {
        RedisZscoreTerminalCommand command = new RedisZscoreTerminalCommand();
        command.key(words[1]);
        command.member(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZscoreTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Double zscore = terminal.client().zscore(null, command.key(), command.member());
            result.setResult(RedisTerminalUtil.formatOut(zscore));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZSCORE";
    }

    @Override
    public String commandDesc() {
        return "获取zset成员分数";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
