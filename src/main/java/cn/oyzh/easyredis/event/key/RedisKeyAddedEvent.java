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
 * @since 2023/11/20
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeyAddedEvent extends Event<RedisConnect> implements EventFormatter {

    private String type;

    private String key;

    private int dbIndex;

    @Override
    public String eventFormat() {
        return String.format(
                "[%s] " + I18nHelper.addKey() + "[%s-db%s] " + I18nHelper.keyType() + ":[%s] ",
                this.data().getName(), this.dbIndex, this.key, this.type
        );
    }
}
