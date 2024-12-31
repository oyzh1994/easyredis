package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/11
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyTTLUpdatedEvent extends Event<RedisConnect> implements  EventFormatter {

    private Long ttl;

    private String key;

    private int dbIndex;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] "+ I18nHelper.ttlUpdated() +"[%s-db%s] ttl:%s",
                this.data().getName(), this.key, this.dbIndex, this.ttl
        );
    }
}
