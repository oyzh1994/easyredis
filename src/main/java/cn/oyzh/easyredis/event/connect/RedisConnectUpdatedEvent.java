package cn.oyzh.easyredis.event.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/12/08
 */
public class RedisConnectUpdatedEvent extends Event<RedisConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] "+ I18nHelper.connectionUpdated(), this.data().getName());
    }
}
