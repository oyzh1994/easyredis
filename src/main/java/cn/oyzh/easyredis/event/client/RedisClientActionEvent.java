package cn.oyzh.easyredis.event.client;

import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import lombok.Setter;
import lombok.experimental.Accessors;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.args.Rawable;

/**
 * @author oyzh
 * @since 2025-01-01
 */
public class RedisClientActionEvent extends Event<String> implements EventFormatter {

    @Setter
    @Accessors(fluent = true, chain = false)
    private CommandArguments arguments;

    @Override
    public String eventFormat() {
        if (this.data() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.data());
        sb.append(" >");
        for (Rawable argument : this.arguments) {
            sb.append(" ").append(new String(argument.getRaw()));
        }
        return sb.toString();
    }
}
