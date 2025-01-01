package cn.oyzh.easyredis.redis;

import cn.oyzh.common.thread.ThreadLocalUtil;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author oyzh
 * @since 2025/01/01
 */
public class RedisPoolManager {

    /**
     * 连接名称
     */
    @Setter
    private String connectName;

    /**
     * 连接池
     */
    @Getter
    @Setter
    private JedisPool jedisPool;

    /**
     * 最大池上限
     * 默认16
     */
    @Getter
    @Setter
    private byte maxPoolSize = 16;

    /**
     * 资源集合
     */
    private final List<Jedis> resources = new ArrayList<>(16);

    /**
     * 获取连接
     *
     * @return Jedis
     */
    public Jedis getResource() {
        try {
            if (!this.resources.isEmpty()) {
                Jedis jedis = CollectionUtil.getRandom(this.resources);
                if (jedis != null) {
                    return jedis;
                }
            }
            if (this.jedisPool != null) {
                Jedis jedis = this.jedisPool.getResource();
                this.resources.add(jedis);
                return jedis;
            }
        } finally {
            ThreadLocalUtil.setVal("connectName", this.connectName);
        }
        return null;
    }

    /**
     * 获取连接
     *
     * @return Jedis
     */
    public Jedis getResource(int dbIndex) {
        try {
            for (Jedis resource : this.resources) {
                if (resource.getDB() == dbIndex) {
                    return resource;
                }
            }
        } finally {
            ThreadLocalUtil.setVal("connectName", this.connectName);
        }
        return this.getResource();
    }

    /**
     * 返还连接
     *
     * @param jedis 连接
     */
    public void returnResource(Jedis jedis) {
        // 默认在dbSize上限前的连接不回收，maxPoolSize默认=16
        if (jedis != null && this.resources.size() > this.maxPoolSize) {
            // 寻找db一样的连接，优先回收
            boolean beReturn = false;
            for (Jedis resource : this.resources) {
                if (resource.getDB() == jedis.getDB()) {
                    beReturn = true;
                    break;
                }
            }
            Jedis jedis1;
            // 优先回收当前
            if (beReturn) {
                jedis1 = jedis;
            } else {// 随机寻找一个连接去回收
                jedis1 = CollectionUtil.getRandom(this.resources);
            }
            if (jedis1 != null) {
                ThreadUtil.startVirtual(() -> {
                    this.doReturnResource(jedis1);
                    this.resources.remove(jedis1);
                });
            }
        }
    }

    /**
     * 执行返回连接
     *
     * @param jedis 连接
     */
    private void doReturnResource(Jedis jedis) {
        if (jedis != null && this.jedisPool != null) {
            this.jedisPool.returnResource(jedis);
        }
    }

    /**
     * 销毁
     */
    public void destroy() {
        for (Jedis value : this.resources) {
            this.doReturnResource(value);
        }
        this.resources.clear();
        // 关闭连接池
        if (this.jedisPool != null && !this.jedisPool.isClosed()) {
            this.jedisPool.close();
            this.jedisPool = null;
        }
    }
}
