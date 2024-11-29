package cn.oyzh.easyredis.terminal.other.client;

/**
 * @author oyzh
 * @since 2023/7/21
 */

public class RedisClientNotouchTerminalCommandHandler extends RedisClientTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "NO-TOUCH";
    }
}
