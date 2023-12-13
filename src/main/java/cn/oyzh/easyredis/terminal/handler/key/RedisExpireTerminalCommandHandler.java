package cn.oyzh.easyredis.terminal.handler.key;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.key.RedisExpireTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisExpireTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisExpireTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3 || words.length == 4;
    }

    @Override
    protected RedisExpireTerminalCommand parseCommand(String line, String[] words) {
        RedisExpireTerminalCommand command = new RedisExpireTerminalCommand();
        command.key(words[1]);
        command.time(Long.parseLong(words[2]));
        if (words.length == 4) {
            command.optionOfString(words[3]);
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisExpireTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long count = terminal.client().expire(null, command.key(), command.time(), command.option());
            result.setResult(RedisShellUtil.formatOut(count));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "EXPIRE";
    }

    @Override
    public String commandArg() {
        return "key seconds [NX | XX | GT | LT]";
    }

    @Override
    public String commandDesc() {
        return "设置过期时间";
    }

}
