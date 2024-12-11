package cn.oyzh.easyredis.handler;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.util.RedisKeyUtil;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author oyzh
 * @since 2024/10/15
 */
@Setter
@Accessors(fluent = true, chain = true)
public class RedisDataTransportHandler extends DataHandler {

    /**
     * 来源客户端
     */
    protected RedisClient sourceClient;

    /**
     * 目标客户端
     */
    protected RedisClient targetClient;

    /**
     * 节点存在时处理策略
     * 0 跳过
     * 1 更新
     */
    private String existsPolicy;

    /**
     * 过滤内容列表
     */
    private List<RedisFilter> filters;

    /**
     * 来源数据库
     */
    private int sourceDatabase;

    /**
     * 目标数据库
     */
    private int targetDatabase;

    /**
     * 键类型
     */
    private List<String> keyTypes;

    /**
     * 保留ttl
     */
    private boolean retainTTL;

    /**
     * 查询模式
     */
    private String pattern = "*";

    /**
     * 执行传输
     */
    public void doTransport() throws Exception {
        this.message("Transport Starting");
        Set<String> allKeys = this.sourceClient.allKeys(this.sourceDatabase, this.pattern);
        this.doTransport(this.sourceDatabase, this.targetDatabase, allKeys);
        this.message("Transport Finished");
    }

    /**
     * 执行传输
     *
     * @param fromDBIndex   来源数据库索引
     * @param targetDBIndex 目标数据库索引
     * @param keys          键列表
     */
    private void doTransport(int fromDBIndex, int targetDBIndex, Set<String> keys) throws InterruptedException {
        for (String key : keys) {
            // 检查操作
            this.checkInterrupt();
            // 被过滤
            if (RedisKeyUtil.isFiltered(key, this.filters)) {
                this.message("key[ " + key + "] is filtered, skip it");
                this.processedSkip();
                continue;
            }
            // 获取键
            RedisKey redisKey = RedisKeyUtil.getKey(fromDBIndex, key, this.retainTTL, true, this.sourceClient);
            // 获取键失败
            if (redisKey == null) {
                this.message("key[ " + key + "] does not exist");
                this.processedIncr();
                continue;
            }
            // 键被排除
            if (this.isExclude(redisKey)) {
                this.message("key[ " + key + "] is exclude, skip it");
                this.processedSkip();
                continue;
            }
            // 键不存在，创建
            if (!this.targetClient.exists(targetDBIndex, key)) {
                this.createKey(redisKey, targetDBIndex);
                this.processedIncr();
                this.message("key[ " + key + "] is not exists, create it");
                continue;
            }
            // 键存在，跳过
            if (StringUtil.equals(this.existsPolicy, "0")) {
                this.processedSkip();
                this.message("key[ " + key + "] is exists, skip it");
                continue;
            }
            // 键存在，更新
            this.targetClient.del(targetDBIndex, key);
            this.createKey(redisKey, targetDBIndex);
            this.processedIncr();
            this.message("key[ " + key + "] is exists, update it");
        }
    }

    /**
     * 是否被排除
     *
     * @param node 键
     * @return 结果
     */
    private boolean isExclude(RedisKey node) {
        if (CollectionUtil.isEmpty(this.keyTypes)) {
            return true;
        }
        if (!this.keyTypes.contains("list") && node.isListKey()) {
            return true;
        }
        if (!this.keyTypes.contains("set") && node.isSetKey()) {
            return true;
        }
        if (!this.keyTypes.contains("zset") && node.isZSetKey()) {
            return true;
        }
        if (!this.keyTypes.contains("hash") && node.isHashKey()) {
            return true;
        }
        if (!this.keyTypes.contains("stream") && node.isStreamKey()) {
            return true;
        }
        return !this.keyTypes.contains("string") && node.isStringKey();
    }

    /**
     * 创建键
     *
     * @param redisKey      redis键
     * @param targetDBIndex 目标数据库索引
     */
    private void createKey(RedisKey redisKey, int targetDBIndex) {
        if (redisKey != null) {
            RedisKeyUtil.createKey(redisKey, targetDBIndex, this.targetClient);
            String key = redisKey.key();
            Long ttl = redisKey.ttl();
            if (ttl != null && this.retainTTL) {
                if (ttl >= 0) {
                    this.targetClient.expire(targetDBIndex, key, ttl, null);
                } else if (ttl == -1) {
                    this.targetClient.persist(targetDBIndex, key);
                }
            }
        }
    }
}

