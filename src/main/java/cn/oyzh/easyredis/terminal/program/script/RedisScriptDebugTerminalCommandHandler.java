package cn.oyzh.easyredis.terminal.program.script;

/**
 * @author oyzh
 * @since 2023/7/31
 */

public class RedisScriptDebugTerminalCommandHandler extends RedisScriptTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "DEBUG";
    }
}
