package cn.oyzh.easyredis.redis;

import cn.oyzh.easyredis.domain.RedisConnect;
import lombok.experimental.UtilityClass;

/**
 * redis客户端工具类
 *
 * @author oyzh
 * @since 2024/12/10
 */
@UtilityClass
public class RedisClientUtil {

    public static RedisClient newClient(RedisConnect zkInfo) {
        return new RedisClient(zkInfo);
    }
}
