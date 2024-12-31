package cn.oyzh.easyredis.terminal.latency;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisLatencyHistogramTerminalCommandHandler extends RedisLatencyTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "HISTOGRAM";
    }
}
