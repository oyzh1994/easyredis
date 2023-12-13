package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisSlowlogLenTerminalCommandHandler extends RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long slowlogLen = terminal.client().slowlogLen();
            result.setResult(RedisShellUtil.formatOut(slowlogLen));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "SLOWLOG";
    }

    @Override
    public String commandSubName() {
        return "LEN";
    }

    @Override
    public String commandDesc() {
        return "获取慢查日志数量";
    }
}
