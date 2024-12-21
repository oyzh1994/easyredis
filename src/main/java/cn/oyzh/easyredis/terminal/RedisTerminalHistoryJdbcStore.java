package cn.oyzh.easyredis.terminal;

import cn.oyzh.store.jdbc.JdbcStandardStore;

/**
 * @author oyzh
 * @since 2024-11-25
 */
public class RedisTerminalHistoryJdbcStore extends JdbcStandardStore<RedisTerminalHistory> {

    /**
     * 当前实例
     */
    public static final RedisTerminalHistoryJdbcStore INSTANCE = new RedisTerminalHistoryJdbcStore();

    public boolean replace(RedisTerminalHistory model) {
        return this.insert(model);
    }

    @Override
    protected RedisTerminalHistory newModel() {
        return new RedisTerminalHistory();
    }

    @Override
    protected Class<RedisTerminalHistory> modelClass() {
        return RedisTerminalHistory.class;
    }
}
