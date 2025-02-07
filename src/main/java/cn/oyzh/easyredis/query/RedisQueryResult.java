package cn.oyzh.easyredis.query;

import cn.oyzh.i18n.I18nHelper;
import lombok.Data;

import java.util.Collection;

/**
 * zk查询结果
 *
 * @author oyzh
 * @since 2025/01/20
 */
@Data
public class RedisQueryResult {

    /**
     * 耗时
     */
    private long cost;

    /**
     * 结果
     */
    private Object result;

    /**
     * 消息
     */
    private String message;

    /**
     * 是否成功
     */
    private boolean success;

    public String costSeconds() {
        return String.format("%.2f" + I18nHelper.seconds(), this.cost / 1000.0);
    }

    public boolean hasData() {
        return this.result instanceof Collection;
    }

    public Collection<?> asData() {
        if (this.result instanceof Collection) {
            return (Collection<?>) this.result;
        }
        return null;
    }
}
