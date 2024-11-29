package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class RedisConnectionConnectedEvent extends Event<RedisClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nHelper.connectionConnected(), this.data().infoName());
    }

    public RedisConnect info() {
        return this.data().redisInfo();
    }
}
