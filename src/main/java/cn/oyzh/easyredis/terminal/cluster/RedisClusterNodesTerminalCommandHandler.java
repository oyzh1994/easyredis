package cn.oyzh.easyredis.terminal.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterNodesTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "NODES";
    }
}
