package cn.oyzh.easyredis.terminal.other.cluster;

import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/7/31
 */
@Component
public class RedisClusterSetslotTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "SETSLOT";
    }
}
