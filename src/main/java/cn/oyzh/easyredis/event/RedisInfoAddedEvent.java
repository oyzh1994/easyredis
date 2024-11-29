package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/12/08
 */
public class RedisInfoAddedEvent extends Event<RedisConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] "+ I18nHelper.connectionAdded(), this.data().getName());
    }
}
