package cn.oyzh.easyredis.terminal.handler.server;

import cn.hutool.json.JSONUtil;
import cn.oyzh.easyredis.terminal.RedisShellUtil;
import cn.oyzh.easyredis.terminal.RedisTerminalTextArea;
import cn.oyzh.easyredis.terminal.command.server.RedisCommandInfoTerminalCommand;
import cn.oyzh.easyredis.terminal.handler.RedisTerminalCommandHandler;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.springframework.stereotype.Component;
import redis.clients.jedis.resps.CommandInfo;

import java.util.Map;

/**
 * @author oyzh
 * @since 2023/7/28
 */
@Component
public class RedisCommandInfoTerminalCommandHandler extends RedisTerminalCommandHandler<RedisCommandInfoTerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 3;
    }

    @Override
    protected RedisCommandInfoTerminalCommand parseCommand(String line, String[] words) {
        RedisCommandInfoTerminalCommand command = new RedisCommandInfoTerminalCommand();
        command.commands(ArrUtil.sub(words, 1));
        return command;
    }

    @Override
    public TerminalExecuteResult execute(RedisCommandInfoTerminalCommand command, RedisTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            Map<String, CommandInfo> info = terminal.client().commandInfo(command.commands());
            result.setResult(RedisShellUtil.formatOut(JSONUtil.toJsonStr(info)));
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
        return "INFO";
    }

    @Override
    public String commandDesc() {
        return "获取命令信息";
    }
}
