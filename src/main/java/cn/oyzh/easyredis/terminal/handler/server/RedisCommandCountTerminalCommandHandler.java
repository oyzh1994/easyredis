package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/28
 */
@Component
public class RedisCommandCountTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long commandCount = terminal.client().commandCount();
            result.setResult(RedisTerminalUtil.formatOut(commandCount));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "COMMAND";
    }

    @Override
    public String commandSubName() {
        return "COUNT";
    }

    @Override
    public String commandDesc() {
        return "获取命令总数";
    }
}
