package cn.oyzh.easyredis.util;

import cn.oyzh.easyredis.command.RedisCommand;
import cn.oyzh.easyredis.command.RedisCommandUtil;
import cn.oyzh.easyredis.exception.UnsupportedCommandException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * redis版本工具类
 *
 * @author oyzh
 * @since 2023/07/31
 */
@UtilityClass
public class RedisVersionUtil {

    /**
     * 版本缓存
     */
    private final static Map<String, Double> VERSION_CACHE = new HashMap<>();

    /**
     * 获取支持的版本
     *
     * @param command 指令
     * @return 支持的版本
     */
    public static String getSupportedVersion(String command) {
        RedisCommand redisCommand = RedisCommandUtil.getCommand(command);
        return redisCommand == null ? "1.0.0" : redisCommand.getAvailable();
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
     * 判断指令是否支持
     *
     * @param serverVersion 服务版本
     * @param command       指令
     */
    public static boolean isCommandSupported(String serverVersion, String command) {
        String version = getSupportedVersion(command);
        return isSupported(serverVersion, version);
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
