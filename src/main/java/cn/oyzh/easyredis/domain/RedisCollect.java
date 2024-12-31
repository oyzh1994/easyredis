package cn.oyzh.easyredis.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Data
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

}
