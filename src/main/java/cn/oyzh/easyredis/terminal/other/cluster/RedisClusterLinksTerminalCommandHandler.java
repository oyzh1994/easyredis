package cn.oyzh.easyredis.terminal.other.cluster;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisClusterLinksTerminalCommandHandler extends RedisClusterTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "LINKS";
    }
}
