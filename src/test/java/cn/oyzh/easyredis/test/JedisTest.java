package cn.oyzh.easyredis.test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

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

    @Test
    public void test3() {
        Jedis jedis = new Jedis("120.24.176.61", 16379);
        jedis.auth("123456");
        jedis.select(2);
        String key = "test".repeat(1_000);
        String res = jedis.set("test1", key);
        System.out.println(res);

        String key1 = "test".repeat(10_000);
        String res1 = jedis.set("test2", key1);
        System.out.println(res1);

        String key2 = "test".repeat(100_000);
        String res2 = jedis.set("test3", key2);
        System.out.println(res2);
    }
}
