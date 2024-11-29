package cn.oyzh.easyredis.terminal;


import cn.oyzh.fx.terminal.histroy.BaseTerminalHistoryHandler;
import cn.oyzh.fx.terminal.histroy.TerminalHistory;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class RedisTerminalHistoryHandler extends BaseTerminalHistoryHandler {

    /**
     * 当前实例
     */
    public static final RedisTerminalHistoryHandler INSTANCE = new RedisTerminalHistoryHandler();


    @Override
    public void clearHistory() {

    }

    @Override
    public List<? extends TerminalHistory> listHistory() {
        return List.of();
    }

    @Override
    public void addHistory(TerminalHistory history) {

    }
}
