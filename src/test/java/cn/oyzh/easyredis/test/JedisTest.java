package cn.oyzh.easyredis.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

/**
 * @author oyzh
 * @since 2024-12-20
 */
public class JedisTest {

    @Test
    public void test() {
        Jedis jedis = new Jedis("127.0.0.1", 7381);
        jedis.auth("123456");
        jedis.select(3);
        String key = "test";
        String value = "test".repeat(50_000_000);
        // String key = "test".repeat(1_000_000);
        String res = jedis.set(key, value);
        System.out.println(res);
    }

    @Test
    public void test2() {
        Jedis jedis = new Jedis("127.0.0.1", 7381);
        jedis.auth("123456");
        jedis.select(3);
        String key = "test".repeat(1_000_000);
        long res = jedis.del(key);
        System.out.println(res);
    }
}
