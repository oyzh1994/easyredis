package cn.oyzh.easyredis.terminal.handler.pubsub;

import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.pubsub.RedisPubsubNumSubTerminalCommand;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisPubsubNumSubTerminalCommandHandler extends BaseTerminalCommandHandler<RedisPubsubNumSubTerminalCommand, RedisTerminalTextArea> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisPubsubNumSubTerminalCommand parseCommand(String line, String[] words) {
        RedisPubsubNumSubTerminalCommand command = new RedisPubsubNumSubTerminalCommand();
        command.channels(ArrUtil.sub(words, 2));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisPubsubNumSubTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Map<String, Long> pubsubChannels = terminal.client().pubsubNumSub(command.channels());
            result.setResult(RedisShellUtil.formatOut(pubsubChannels));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PUBSUB";
    }

    @Override
    public String commandSubName() {
        return "NUMSUB";
    }

    @Override
    public String commandArg() {
        return "channel [channel...]";
    }

    @Override
    public String commandDesc() {
        return "获取通道的订阅数量";
    }
}
