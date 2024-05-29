package cn.oyzh.easyredis.terminal.handler.geo;

import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.terminal.handler.RedisKeyTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/26
 */
@Component
public class RedisGeoposTerminalCommandHandler extends RedisKeyTerminalCommandHandler<TerminalCommand> {

    @Override
    public String commandArg() {
        return "key [member [member ...]]";
    }

    @Override
    public String commandDesc() {
        return "获取geo坐标的经纬度";
    }

    @Override
    protected RedisKeyType getKeyType() {
        return RedisKeyType.ZSET;
    }

    @Override
    protected Protocol.Command getCommandType() {
        return Protocol.Command.GEOPOS;
    }
}
