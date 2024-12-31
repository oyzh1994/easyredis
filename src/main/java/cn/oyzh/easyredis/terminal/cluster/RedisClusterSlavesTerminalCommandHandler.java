package cn.oyzh.easyredis.terminal.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterSlavesTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "SLAVES";
    }
}
