package cn.oyzh.easyredis.terminal;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.RedisConst;
import cn.oyzh.fx.terminal.histroy.TerminalHistoryStore;

/**
 * redis终端命令历史
 *
 * @author oyzh
 * @since 2023/7/21
 */
//@Slf4j
@Deprecated
public class RedisTerminalHistoryStore extends TerminalHistoryStore {

    /**
     * 当前实例
     */
    public static final RedisTerminalHistoryStore INSTANCE = new RedisTerminalHistoryStore();

    {
        this.filePath(RedisConst.STORE_PATH + "redis_shell_history.json");
        JulLog.info("RedisShellHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

}
