package cn.oyzh.easyredis.redis;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.DefaultJedisClientConfig;

/**
 * redis客户端工具类
 *
 * @author oyzh
 * @since 2024/12/10
 */
@UtilityClass
public class RedisClientUtil {

    /**
     * 初始化客户端配置
     *
     * @param user     用户
     * @param password 密码
     */
    public DefaultJedisClientConfig newConfig(String user, String password, int connectTimeout, int socketTimeout) {
        // master配置处理
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        // socket超时
        builder.socketTimeoutMillis(socketTimeout);
        // 连接超时
        builder.connectionTimeoutMillis(connectTimeout);
        // 连接用户
        if (StringUtil.isNotBlank(user)) {
            builder.user(user);
        }
        // 连接密码
        if (StringUtil.isNotBlank(password)) {
            builder.password(password);
        }
        return builder.build();
    }

    public static RedisClient newClient(RedisConnect zkInfo) {
        return new RedisClient(zkInfo);
    }
}
