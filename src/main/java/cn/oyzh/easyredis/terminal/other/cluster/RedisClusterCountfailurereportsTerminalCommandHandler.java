package cn.oyzh.easyredis.terminal.other.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterCountfailurereportsTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "COUNT-FAILURE-REPORTS";
    }
}
