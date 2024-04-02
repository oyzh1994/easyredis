package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.command.server.RedisWaitTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisWaitTerminalCommandHandler extends RedisTerminalCommandHandler<RedisWaitTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) throws RuntimeException {
        return words.length == 3;
    }

    @Override
    protected RedisWaitTerminalCommand parseCommand(String line, String[] words) throws RuntimeException {
        RedisWaitTerminalCommand command = new RedisWaitTerminalCommand();
        command.replicas(Integer.parseInt(words[1]));
        command.timeout(Long.parseLong(words[2]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisWaitTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long waitReplicas = terminal.client().waitReplicas(command.replicas(), command.timeout());
            result.setResult(RedisTerminalUtil.formatOut(waitReplicas));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "WAIT";
    }

    @Override
    public String commandArg() {
        return "numreplicas timeout";
    }

    @Override
    public String commandDesc() {
        return "等待副本写入";
    }
}
