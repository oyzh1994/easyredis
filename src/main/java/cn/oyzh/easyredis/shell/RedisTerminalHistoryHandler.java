package cn.oyzh.easyredis.shell;


import cn.oyzh.fx.terminal.histroy.BaseTerminalHistoryHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class RedisTerminalHistoryHandler extends BaseTerminalHistoryHandler {

    /**
     * 当前实例
     */
    public static final RedisTerminalHistoryHandler INSTANCE = new RedisTerminalHistoryHandler();

    public RedisTerminalHistoryHandler() {
        super(RedisTerminalHistoryStore.INSTANCE);
    }
}
