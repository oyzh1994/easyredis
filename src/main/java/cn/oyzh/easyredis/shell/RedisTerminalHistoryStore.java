package cn.oyzh.easyredis.shell;

import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.fx.terminal.histroy.TerminalHistoryStore;
import lombok.extern.slf4j.Slf4j;

/**
 * redis终端命令历史
 *
 * @author oyzh
 * @since 2023/7/21
 */
@Slf4j
public class RedisTerminalHistoryStore extends TerminalHistoryStore {

    /**
     * 当前实例
     */
    public static final RedisTerminalHistoryStore INSTANCE = new RedisTerminalHistoryStore();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_shell_history.json");
        log.info("RedisShellHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

}
