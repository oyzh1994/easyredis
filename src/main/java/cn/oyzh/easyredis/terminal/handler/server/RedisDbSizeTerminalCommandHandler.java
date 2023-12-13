package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/25
 */
@Component
public class RedisDbSizeTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {


    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long dbSize = terminal.client().dbSize((Integer) null);
            result.setResult(RedisShellUtil.formatOut(dbSize));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "DBSIZE";
    }

    @Override
    public String commandDesc() {
        return "获取键数量";
    }
}
