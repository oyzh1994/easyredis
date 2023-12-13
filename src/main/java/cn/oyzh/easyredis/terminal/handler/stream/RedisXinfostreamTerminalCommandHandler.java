package cn.oyzh.easyredis.terminal.handler.stream;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.stream.RedisXinfostreamTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.resps.StreamFullInfo;
import redis.clients.jedis.resps.StreamInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisXinfostreamTerminalCommandHandler extends RedisKeyTerminalCommandHandler<RedisXinfostreamTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 2 || words.length == 3;
    }

    @Override
    protected RedisXinfostreamTerminalCommand parseCommand(String line, String[] words) {
        RedisXinfostreamTerminalCommand command = new RedisXinfostreamTerminalCommand();
        command.key(words[1]);
        if (words.length == 3) {
            command.full(StrUtil.endWithIgnoreCase(words[2], "full"));
        }
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisXinfostreamTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {

            Map<String, Object> infoMap;

            if (Boolean.TRUE == command.full()) {
                StreamFullInfo info = terminal.client().xinfoStreamFull(null, command.key());
                infoMap = info.getStreamFullInfo();
            } else {
                StreamInfo info = terminal.client().xinfoStream(null, command.key());
                infoMap = info.getStreamInfo();
            }
            List<Object> list = new ArrayList<>();
            for (String s : infoMap.keySet()) {
                list.add(s);
                list.add(infoMap.get(s));
            }
            result.setResult(RedisShellUtil.formatOut(list));
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "XINFOSTREAM";
    }

    @Override
    public String commandDesc() {
        return "获取stream信息";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.STREAM;
    }
}
