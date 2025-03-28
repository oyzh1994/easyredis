package cn.oyzh.easyredis.event.window;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2025-02-20
 */
public class RedisShowExportDataEvent extends Event<RedisConnect> {
    public Integer getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(Integer dbIndex) {
        this.dbIndex = dbIndex;
    }

    private Integer dbIndex;
}
