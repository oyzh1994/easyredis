package cn.oyzh.easyredis.terminal.handler.server;

import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.server.RedisWaitAofTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisWaitAofTerminalCommandHandler extends RedisTerminalCommandHandler<RedisWaitAofTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) throws RuntimeException {
        return words.length == 4;
    }

    @Override
    protected RedisWaitAofTerminalCommand parseCommand(String line, String[] words) throws RuntimeException {
        RedisWaitAofTerminalCommand command = new RedisWaitAofTerminalCommand();
        command.numLocal(Integer.parseInt(words[1]));
        command.replicas(Integer.parseInt(words[2]));
        command.timeout(Long.parseLong(words[3]));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisWaitAofTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            KeyValue<Long, Long> waitAOF = terminal.client().waitAOF(command.numLocal(), command.replicas(), command.timeout());
            List<Long> list=new ArrayList<>();
            list.add(waitAOF.getKey());
            list.add(waitAOF.getValue());
            result.setResult(RedisTerminalUtil.formatOut(list));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "WAITAOF";
    }

    @Override
    public String commandArg() {
        return "numlocal numreplicas timeout";
    }

    @Override
    public String commandDesc() {
        return "等待副本保存";
    }
}
