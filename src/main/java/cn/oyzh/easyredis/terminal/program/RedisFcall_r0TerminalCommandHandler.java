package cn.oyzh.easyredis.terminal.program;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisFcall_r0TerminalCommandHandler extends cn.oyzh.easyredis.terminal.RedisTerminalCommandHandler<TerminalCommand> {

    @Override
    public Protocol.Command getCommandType() {
        return Protocol.Command.FCALL_RO;
    }
}
