package cn.oyzh.easyredis.domain;

import cn.oyzh.ssh.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Table("t_ssh_config")
public class RedisSSHConfig extends SSHConnect implements Serializable {

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    /**
     * 连接id
     * @see RedisConnect
     */
    @Column
    @PrimaryKey
    private String iid;
}
