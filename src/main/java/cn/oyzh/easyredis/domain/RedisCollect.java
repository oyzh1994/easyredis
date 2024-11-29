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
     * 路径
     */
    @Column
    private String path;

    public RedisCollect() {

    }

    public RedisCollect(String iid, String path) {
        this.iid = iid;
        this.path = path;
    }

}
