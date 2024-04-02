package cn.oyzh.easyredis.terminal.handler.zset;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.zset.RedisZrevrankTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisZrevrankTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisZrevrankTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }


    @Override
    protected RedisZrevrankTerminalCommand parseCommand(String line, String[] words) {
        RedisZrevrankTerminalCommand command = new RedisZrevrankTerminalCommand();
        command.key(words[1]);
        command.member(words[2]);
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisZrevrankTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Long zrevrank = terminal.client().zrevrank(null, command.key(), command.member());
            result.setResult(RedisTerminalUtil.formatOut(zrevrank));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "ZREVRANK";
    }

    @Override
    public String commandArg() {
        return "key member";
    }

    @Override
    public String commandDesc() {
        return "获取zset成员排名，从大小到小排序";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
