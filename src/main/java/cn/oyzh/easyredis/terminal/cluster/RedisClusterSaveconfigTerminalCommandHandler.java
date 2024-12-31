package cn.oyzh.easyredis.terminal.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterSaveconfigTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "SAVECONFIG";
    }
}
