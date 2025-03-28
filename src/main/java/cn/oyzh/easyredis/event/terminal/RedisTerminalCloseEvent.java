package cn.oyzh.easyredis.event.terminal;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/11/20
 */
public class RedisTerminalCloseEvent extends Event<RedisConnect> {

    private Integer dbIndex;

    public Integer getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(Integer dbIndex) {
        this.dbIndex = dbIndex;
    }
}
