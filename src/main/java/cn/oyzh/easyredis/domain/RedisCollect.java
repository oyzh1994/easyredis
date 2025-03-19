package cn.oyzh.easyredis.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Table("t_collect")
public class RedisCollect implements Serializable {

    /**
     * 信息id
     */
    @Column
    private String iid;

    /**
     * db索引
     */
    @Column
    private int dbIndex;

    /**
     * 键
     */
    @Column
    private String key;

    public RedisCollect() {

    }

    public RedisCollect(String iid, int dbIndex, String key) {
        this.iid = iid;
        this.key = key;
        this.dbIndex = dbIndex;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public void setDbIndex(int dbIndex) {
        this.dbIndex = dbIndex;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
