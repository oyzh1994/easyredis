package cn.oyzh.easyredis.terminal.other.client;

import cn.oyzh.easyredis.terminal.other.slowlog.RedisSlowlogTerminalCommandHandler;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisClientInfoTerminalCommandHandler extends RedisClientTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return Protocol.Keyword.INFO.name();
    }
}
