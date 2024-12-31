package cn.oyzh.easyredis.terminal.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterMyidTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "MYID";
    }
}
