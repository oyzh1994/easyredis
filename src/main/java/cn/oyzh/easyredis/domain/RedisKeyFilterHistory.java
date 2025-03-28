package cn.oyzh.easyredis.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * redis键过滤历史
 *
 * @author oyzh
 * @since 2023/07/19
 */
@Table("t_key_filter_history")
public class RedisKeyFilterHistory implements ObjectComparator<RedisKeyFilterHistory>, Serializable {
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String uid;

    public RedisKeyFilterHistory() {
    }

    public RedisKeyFilterHistory(String uid, String pattern) {
        this.uid = uid;
        this.pattern = pattern;
    }

    /**
     * 模式
     */
    @Column
    private String pattern;

    @Override
    public boolean compare(RedisKeyFilterHistory t1) {
        if (t1 == null) {
            return false;
        }
        if (Objects.equals(this, t1)) {
            return true;
        }
        return Objects.equals(this.pattern, t1.pattern);
    }
}
