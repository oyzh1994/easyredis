package cn.oyzh.easyredis.terminal.handler.pubsub;

import cn.hutool.core.util.ArrayUtil;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.pubsub.RedisPublishTerminalCommand;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/08/02
 */
@Component
public class RedisPublishTerminalCommandHandler extends BaseTerminalCommandHandler<RedisPublishTerminalCommand, RedisTerminalTextArea> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length > 2;
    }

    @Override
    protected RedisPublishTerminalCommand parseCommand(String line, String[] words) {
        RedisPublishTerminalCommand command = new RedisPublishTerminalCommand();
        command.channel(words[1]);
        command.message(ArrayUtil.join(ArrUtil.sub(words, 2), ""));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisPublishTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            long publish = terminal.client().publish(command.channel(), command.message());
            result.setResult(RedisShellUtil.formatOut(publish));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "PUBLISH";
    }

    @Override
    public String commandArg() {
        return "channel message";
    }

    @Override
    public String commandDesc() {
        return "发布消息";
    }
}
