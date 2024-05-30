package cn.oyzh.easyredis.terminal.other.client;

import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/21
 */
@Component
public class RedisClientGetredirTerminalCommandHandler extends RedisClientTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "GETREDIR";
    }
}
