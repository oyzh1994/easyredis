package cn.oyzh.easyredis.info;

import cn.oyzh.easyredis.redis.RedisClient;
import lombok.Data;

/**
 * 订阅发布项目
 *
 * @author oyzh
 * @since 2023/8/02
 */
@Data
public class RedisPubsubItem {

    /**
     * 编号
     */
    private int index;

    /**
     * 通道
     */
    private String channel;

    /**
     * redis客户端
     */
    private RedisClient client;

}
