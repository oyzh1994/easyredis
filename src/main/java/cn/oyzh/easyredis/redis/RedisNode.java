package cn.oyzh.easyredis.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis节点
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisNode implements Comparable<RedisNode>{

    /**
     * db索引
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private int dbIndex;

    /**
     * key名称
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private String key;

    /**
     * 节点类型
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisNodeType type;

    /**
     * 数据是否已初始化
     */
    protected Boolean valueInitialized;

    /**
     * 数据是否已初始化
     *
     * @return 结果
     */
    public boolean isValueInitialized() {
        return this.valueInitialized != null && this.valueInitialized;
    }

    /**
     * 设置节点类型
     *
     * @param type 节点类型
     */
    public void type(RedisNodeType type) {
        this.type = type;
    }

    /**
     * 设置节点类型
     *
     * @param type 节点类型
     */
    public void type(String type) {
        this.type = RedisNodeType.valueOfType(type);
    }

    /**
     * 是否string键
     *
     * @return 结果
     */
    public boolean isStringKey() {
        return RedisNodeType.STRING == this.type;
    }

    /**
     * 是否set键
     *
     * @return 结果
     */
    public boolean isSetKey() {
        return RedisNodeType.SET == this.type;
    }

    /**
     * 是否zset键
     *
     * @return 结果
     */
    public boolean isZSetKey() {
        return RedisNodeType.ZSET == this.type;
    }

    /**
     * 是否list键
     *
     * @return 结果
     */
    public boolean isListKey() {
        return RedisNodeType.LIST == this.type;
    }

    /**
     * 是否hash键
     *
     * @return 结果
     */
    public boolean isHashKey() {
        return RedisNodeType.HASH == this.type;
    }

    /**
     * 是否hyperLogLog键
     *
     * @return 结果
     */
    public boolean isHyperLogLogKey() {
        return RedisNodeType.HYPERLOGLOG == this.type;
    }

    /**
     * 是否stream键
     *
     * @return 结果
     */
    public boolean isStreamKey() {
        return RedisNodeType.STREAM == this.type;
    }

    @Override
    public int compareTo(RedisNode node) {
        if (node == null || node.key() == null) {
            return -1;
        }
        return this.key().compareToIgnoreCase(node.key());
    }
}
