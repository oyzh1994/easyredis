package cn.oyzh.easyredis.redis;

import cn.oyzh.common.thread.ThreadLocalUtil;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * redis集群操作对象
     */
    @Getter
    @Setter
    private JedisCluster cluster;

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
    private List<Jedis> resources;

    /**
     * cluster集群的主节点连接
     */
    @Getter
    private List<ConnectionPool> clusterPools;

    /**
     * 初始化集群连接
     *
     * @param poolMap 连接列表
     */
    public void initClusterPool(Map<String, ConnectionPool> poolMap) {
        if (CollectionUtil.isNotEmpty(poolMap) && (this.clusterPools == null || !poolMap.values().containsAll(this.clusterPools))) {
            this.clusterPools = new ArrayList<>();
            this.clusterPools.addAll(poolMap.values());
        }
    }

    /**
     * 是否有集群连接
     *
     * @return 结果
     */
    public boolean hasClusterPool() {
        return CollectionUtil.isNotEmpty(this.clusterPools);
    }

    /**
     * 获取连接
     *
     * @return Jedis
     */
    public Jedis getResource() {
        try {
            if (CollectionUtil.isNotEmpty(this.resources)) {
                Jedis jedis = CollectionUtil.getRandom(this.resources);
                if (jedis != null) {
                    return jedis;
                }
            }
            if (this.jedisPool != null) {
                Jedis jedis = this.jedisPool.getResource();
                if (this.resources == null) {
                    this.resources = new ArrayList<>();
                }
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
        if (CollectionUtil.isNotEmpty(this.resources)) {
            try {
                for (Jedis resource : this.resources) {
                    if (resource.getDB() == dbIndex) {
                        return resource;
                    }
                }
            } finally {
                ThreadLocalUtil.setVal("connectName", this.connectName);
            }
        }
        return this.getResource();
    }

    /**
     * 返还连接
     *
     * @param jedis 连接
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            // 如果没有超过限制，则不回收
            if (CollectionUtil.isEmpty(this.resources) || this.resources.size() <= this.maxPoolSize) {
                return;
            }
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
        // 清理一般连接
        if (CollectionUtil.isNotEmpty(this.resources)) {
            for (Jedis value : this.resources) {
                this.doReturnResource(value);
            }
            this.resources.clear();
            this.resources = null;
        }
        // 关闭连接池
        if (this.jedisPool != null && !this.jedisPool.isClosed()) {
            this.jedisPool.close();
            this.jedisPool = null;
        }
        // 关闭集群连接
        if (this.cluster != null) {
            this.cluster.close();
            this.cluster = null;
        }
        // 清理哨兵连接
        if (CollectionUtil.isNotEmpty(this.clusterPools)) {
            for (ConnectionPool pool : this.clusterPools) {
                pool.close();
            }
            this.clusterPools.clear();
            this.clusterPools = null;
        }
    }
}
