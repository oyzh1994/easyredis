package cn.oyzh.easyredis.terminal.other.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterSetconfigepochTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "SET-CONFIG-EPOCH";
    }
}
