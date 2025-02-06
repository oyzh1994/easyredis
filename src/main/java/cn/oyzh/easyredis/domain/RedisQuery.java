package cn.oyzh.easyredis.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * zk查询
 *
 * @author oyzh
 * @since 2025-01-20
 */
@Data
@Table("t_query")
public class RedisQuery implements Serializable {

    /**
     * 连接id
     *
     * @see RedisConnect
     */
    @Column
    private String iid;

    /**
     * 主键
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * 名称
     */
    @Column
    private String name;

    /**
     * 内容
     */
    @Column
    private String content;
}
