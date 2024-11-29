package cn.oyzh.easyredis.domain;

import cn.oyzh.common.util.ObjectComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * redis键过滤历史
 *
 * @author oyzh
 * @since 2023/07/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisKeyFilterHistory implements ObjectComparator<RedisKeyFilterHistory> {

    /**
     * 模式
     */
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
