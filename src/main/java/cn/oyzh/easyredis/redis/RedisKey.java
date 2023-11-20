package cn.oyzh.easyredis.redis;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisKey implements Comparable<RedisKey> {

    /**
     * db索引
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private int dbIndex;

    /**
     * ttl值
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private Long ttl;

    /**
     * key名称
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private String key;

    /**
     * 键类型
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisKeyType type;

    /**
     * 空闲时间
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private Long objectIdletime;

    /**
     * 引用数量
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private Long objectRefcount;

    /**
     * 编码值
     */
    @Getter
    @Setter
    @Accessors(chain = true, fluent = true)
    private String objectedEncoding;

    /**
     * 设置键类型
     *
     * @param type 键类型
     */
    public void type(RedisKeyType type) {
        this.type = type;
    }

    /**
     * 设置键类型
     *
     * @param type 键类型
     */
    public void type(String type) {
        this.type = RedisKeyType.valueOfType(type);
    }

    /**
     * 是否string键
     *
     * @return 结果
     */
    public boolean isStringKey() {
        return RedisKeyType.STRING == this.type;
    }

    /**
     * 是否set键
     *
     * @return 结果
     */
    public boolean isSetKey() {
        return RedisKeyType.SET == this.type;
    }

    /**
     * 是否zset键
     *
     * @return 结果
     */
    public boolean isZSetKey() {
        return RedisKeyType.ZSET == this.type;
    }

    /**
     * 是否list键
     *
     * @return 结果
     */
    public boolean isListKey() {
        return RedisKeyType.LIST == this.type;
    }

    /**
     * 是否hash键
     *
     * @return 结果
     */
    public boolean isHashKey() {
        return RedisKeyType.HASH == this.type;
    }

    /**
     * 是否hyperLogLog键
     *
     * @return 结果
     */
    public boolean isHyperLogLogKey() {
        return RedisKeyType.HYPERLOGLOG == this.type;
    }

    /**
     * 是否stream键
     *
     * @return 结果
     */
    public boolean isStreamKey() {
        return RedisKeyType.STREAM == this.type;
    }

    @Override
    public int compareTo(RedisKey node) {
        if (node == null || node.key() == null) {
            return -1;
        }
        return this.key().compareToIgnoreCase(node.key());
    }

    /**
     * 获取空闲时间字符串
     *
     * @return 空闲时间字符串
     */
    public String objectIdletimeString() {
        return this.objectIdletime == null ? "N/A" : this.objectIdletime + "";
    }

    /**
     * 获取字符编码字符串
     *
     * @return 字符编码字符串
     */
    public String objectedEncodingString() {
        return this.objectedEncoding == null ? "N/A" : this.objectedEncoding;
    }

    /**
     * 获取引用计数字符串
     *
     * @return 引用计数字符串
     */
    public String objectRefcountString() {
        return this.objectRefcount == null || this.objectRefcount == Integer.MAX_VALUE ? "N/A" : this.objectRefcount + "";
    }

    /**
     * 是否raw格式
     *
     * @return 结果
     */
    public boolean isRawEncoding() {
        return StrUtil.equalsIgnoreCase("raw", this.objectedEncoding);
    }

    /**
     * 获取键的二进制数据
     *
     * @return 键的二进制数据
     */
    public byte[] keyBinary() {
        return this.key == null ? null : this.key.getBytes();
    }
}
