package cn.oyzh.easyredis.terminal.other.latency;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisLatencyGraphTerminalCommandHandler extends RedisLatencyTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "GRAPH";
    }
}
