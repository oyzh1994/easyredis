package cn.oyzh.easyredis.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


/**
 * redis过滤配置
 *
 * @author oyzh
 * @since 2023/06/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("t_filter")
public class RedisFilter implements ObjectComparator<RedisFilter>, Serializable {

    /**
     * id
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * redis连接id
     *
     * @see RedisConnect
     */
    @Column
    private String iid;

    /**
     * 关键词
     */
    @Column
    private String kw;

    /**
     * 模糊匹配
     * true 模糊匹配
     * false 完全匹配
     */
    @Column
    private boolean partMatch;

    /**
     * 是否启用
     */
    @Column
    private boolean enable;

    /**
     * 复制对象
     *
     * @param filter 过滤信息
     * @return 当前对象
     */
    public RedisFilter copy( RedisFilter filter) {
        this.kw = filter.kw;
        this.iid = filter.iid;
        this.enable = filter.enable;
        this.partMatch = filter.partMatch;
        return this;
    }

    @Override
    public boolean compare(RedisFilter filter) {
        if (Objects.equals(this, filter)) {
            return true;
        }
        return Objects.equals(filter.kw, this.kw);
    }

    /**
     * 比较信息
     *
     * @param kw 关键字
     * @return 结果
     */
    public boolean compare(String kw) {
        return Objects.equals(kw, this.kw);
    }
}
