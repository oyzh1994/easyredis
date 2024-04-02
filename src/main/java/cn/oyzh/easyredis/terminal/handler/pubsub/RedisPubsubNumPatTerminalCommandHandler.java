package cn.oyzh.easyredis.terminal.handler.pubsub;

import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.RedisTerminalUtil;
import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/27
 */
@Component
public class RedisPubsubNumPatTerminalCommandHandler extends BaseTerminalCommandHandler<TerminalCommand, RedisTerminalTextArea> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Long pubsubNumPat = terminal.client().pubsubNumPat();
            result.setResult(RedisTerminalUtil.formatOut(pubsubNumPat));
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
        return "NUMPAT";
    }

    @Override
    public String commandDesc() {
        return "获取订阅及发布的活跃通道数量";
    }
}
