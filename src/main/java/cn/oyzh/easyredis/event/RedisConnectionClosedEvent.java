package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class RedisConnectionClosedEvent extends Event<RedisClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nHelper.connectionDisconnected(), this.data().infoName());
    }

    public RedisConnect info() {
        return this.data().redisInfo();
    }
}
