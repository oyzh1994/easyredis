package cn.oyzh.easyredis.redis;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

/**
 *
 * @author oyzh
 * @since 2025/01/02
 */
@Getter
@Setter
public class RedisConn {

    private Jedis jedis;

    private boolean using;

    public RedisConn(Jedis jedis) {
        this.jedis = jedis;
    }

    public RedisConn(Jedis jedis, boolean using) {
        this.jedis = jedis;
        this.using = using;
    }

    public int getDB() {
        return this.jedis.getDB();
    }
}
