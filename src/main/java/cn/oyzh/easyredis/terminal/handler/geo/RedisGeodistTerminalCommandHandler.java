package cn.oyzh.easyredis.terminal.handler.geo;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.geo.RedisGeodistTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisGeodistTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisGeodistTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 4 || words.length == 5;
    }

    @Override
    protected RedisGeodistTerminalCommand parseCommand(String line, String[] words) {
        RedisGeodistTerminalCommand command = new RedisGeodistTerminalCommand();
        command.key(words[1]);
        command.member1(words[2]);
        command.member2(words[3]);
        if (words.length == 5) {
            command.unitOfString(words[4]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisGeodistTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Double dist = terminal.client().geodist(null, command.key(), command.member1(), command.member2(), command.unit());
            result.setResult(RedisTerminalUtil.formatOut(dist));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "GEODIST";
    }

    @Override
    public String commandArg() {
        return "key member1 member2 [M | KM | FT | MI]";
    }

    @Override
    public String commandDesc() {
        return "计算geo两个坐标的距离";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }
}
