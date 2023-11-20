package cn.oyzh.easyredis.util;

import cn.oyzh.easyredis.exception.UnsupportedCommandException;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * redis版本工具类
 *
 * @author oyzh
 * @since 2023/07/31
 */
@Slf4j
@UtilityClass
public class RedisVersionUtil {

    /**
     * 版本缓存
     */
    private final static Map<String, Double> VERSION_CACHE = new HashMap<>();

    /**
     * 支持版本
     */
    private final static Map<String, String> SUPPORTED_VERSION = new HashMap<>();

    static {

        // key
        SUPPORTED_VERSION.put("COPY", "6.2.0");
        SUPPORTED_VERSION.put("DUMP", "2.6.0");
        SUPPORTED_VERSION.put("PERSIST", "2.2.0");
        SUPPORTED_VERSION.put("EXPIREAT", "1.2.0");
        SUPPORTED_VERSION.put("PEXPIRE", "2.6.0");
        SUPPORTED_VERSION.put("PEXPIREAT", "2.6.0");
        SUPPORTED_VERSION.put("PTTL", "2.6.0");
        SUPPORTED_VERSION.put("TOUCH", "3.2.1");

        // server
        SUPPORTED_VERSION.put("WAIT", "3.0.0");
        SUPPORTED_VERSION.put("WAITAOF", "7.2.0");

        // object
        SUPPORTED_VERSION.put("OBJECT ENCODING", "2.2.3");
        SUPPORTED_VERSION.put("OBJECT FREQ", "4.0.0");
        SUPPORTED_VERSION.put("OBJECT IDLETIME", "2.2.3");
        SUPPORTED_VERSION.put("OBJECT REFCOUNT", "2.2.3");

        // geo
        SUPPORTED_VERSION.put("GEOHASH", "3.2.0");
        SUPPORTED_VERSION.put("GEODIST", "3.2.0");
        SUPPORTED_VERSION.put("GEOPOS", "3.2.0");

        // hash
        SUPPORTED_VERSION.put("HRANDFIELD", "6.2.0");
        SUPPORTED_VERSION.put("HDEL", "2.0.0");
        SUPPORTED_VERSION.put("HEXISTS", "2.0.0");
        SUPPORTED_VERSION.put("HGET", "2.0.0");
        SUPPORTED_VERSION.put("HGETALL", "2.0.0");
        SUPPORTED_VERSION.put("HKEYS", "2.0.0");
        SUPPORTED_VERSION.put("HLEN", "2.0.0");
        SUPPORTED_VERSION.put("HMGET", "2.0.0");
        SUPPORTED_VERSION.put("HMSET", "2.0.0");
        SUPPORTED_VERSION.put("HSETNX", "2.0.0");
        SUPPORTED_VERSION.put("HSTRLEN", "3.2.0");

        // bit
        SUPPORTED_VERSION.put("BITCOUNT", "2.6.0");
        SUPPORTED_VERSION.put("BITPOS", "2.7.8");
        SUPPORTED_VERSION.put("GETBIT", "2.2.0");
        SUPPORTED_VERSION.put("SETBIT", "2.2.0");

        // zset
        SUPPORTED_VERSION.put("ZCARD", "1.2.0");
        SUPPORTED_VERSION.put("ZADD", "1.2.0");
        SUPPORTED_VERSION.put("ZCOUNT", "2.0.0");
        SUPPORTED_VERSION.put("ZDIFF", "6.2.0");
        SUPPORTED_VERSION.put("ZDIFFSTORE", "6.2.0");
        SUPPORTED_VERSION.put("ZMSCORE", "6.2.0");

        // 发布及订阅
        SUPPORTED_VERSION.put("PUBLISH", "2.0.0");
        SUPPORTED_VERSION.put("PSUBSCRIBE", "2.0.0");
        SUPPORTED_VERSION.put("UNSUBSCRIBE", "2.0.0");
        SUPPORTED_VERSION.put("PUNSUBSCRIBE", "2.0.0");
        SUPPORTED_VERSION.put("PUBSUB NUMPAT", "2.8.0");
        SUPPORTED_VERSION.put("PUBSUB NUMSUB", "2.8.0");
        SUPPORTED_VERSION.put("PUBSUB CHANNELS", "2.8.0");
    }

    /**
     * 获取支持的版本
     *
     * @param command 指令
     * @return 支持的版本
     */
    public static String getSupportedVersion(@NonNull String command) {
        String version = SUPPORTED_VERSION.get(command.toUpperCase());
        return version == null ? "1.0.0" : version;
    }

    /**
     * 检查指令是否支持
     *
     * @param serverVersion 服务版本
     * @param command       指令
     */
    public static void checkSupported(String serverVersion, String command) {
        String version = getSupportedVersion(command);
        if (!isSupported(serverVersion, version)) {
            throw new UnsupportedCommandException(serverVersion, version, command);
        }
    }

    /**
     * 判断是否支持
     *
     * @param serverVersion 服务版本
     * @param version       指令版本
     * @return 结果
     */
    public static boolean isSupported(String serverVersion, String version) {
        if (serverVersion != null && version != null) {
            try {
                Double s1 = VERSION_CACHE.get(serverVersion);
                Double s2 = VERSION_CACHE.get(version);
                if (s1 == null) {
                    serverVersion = serverVersion.toLowerCase().replace("v", "");
                    s1 = Double.parseDouble(serverVersion.replace(".", ""));
                    VERSION_CACHE.put(serverVersion, s1);
                }
                if (s2 == null) {
                    version = version.toLowerCase().replace("v", "");
                    s2 = Double.parseDouble(version.replace(".", ""));
                    VERSION_CACHE.put(version, s2);
                }
                return s1 >= s2;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}
