package cn.oyzh.easyredis.terminal.handler.key;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.key.RedisCopyTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisCopyTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisCopyTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3 || words.length == 4 || words.length == 5 || words.length == 6;
    }

    @Override
    protected RedisCopyTerminalCommand parseCommand(String line, String[] words) {
        RedisCopyTerminalCommand command = new RedisCopyTerminalCommand();
        command.key(words[1]);
        command.dstKey(words[2]);
        if ((words.length == 4 || words.length == 6) && StrUtil.equalsIgnoreCase("replace", ArrUtil.last(words))) {
            command.replace(true);
        }
        if ((words.length == 5 || words.length == 6) && StrUtil.equalsIgnoreCase("db", words[3])) {
            command.db(Integer.valueOf(words[4]));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisCopyTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            boolean copy = terminal.client().copy(null, command.key(), command.dstKey(), command.db(), command.replace());
            result.setResult(RedisTerminalUtil.formatOut(copy));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "COPY";
    }

    @Override
    public String commandArg() {
        return "source destination [DB destination-db] [REPLACE]";
    }

    @Override
    public String commandDesc() {
        return "复制键";
    }

}
