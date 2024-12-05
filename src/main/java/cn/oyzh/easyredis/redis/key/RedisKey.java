package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * redis键
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Getter
@Accessors(chain = true, fluent = true)
public class RedisKey implements Comparable<RedisKey> {

    /**
     * db索引
     */
    @Setter
    private int dbIndex;

    /**
     * 加载耗时
     */
    @Setter
    private short loadTime;

    /**
     * ttl值
     */
    @Setter
    private Long ttl;

    /**
     * key名称
     */
    @Setter
    private String key;

    /**
     * 键类型
     */
    private RedisKeyType type;

    /**
     * 空闲时间
     */
    @Setter
    private Long objectIdletime;

    /**
     * 引用数量
     */
    @Setter
    private Long objectRefcount;

    /**
     * 编码值
     */
    @Setter
    private String objectedEncoding;

    /**
     * 设置键类型
     *
     * @param type 键类型
     */
    public void type(RedisKeyType type) {
        this.type = type;
    }
//
//    /**
//     * 设置键类型
//     *
//     * @param type 键类型
//     */
//    public void type(String type) {
//        this.type = RedisKeyType.valueOfType(type);
//    }

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

//    /**
//     * 是否hyperLogLog键
//     *
//     * @return 结果
//     */
//    public boolean isHyLogKey() {
//        return RedisKeyType.HYPERLOGLOG == this.type;
//    }

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
        return StringUtil.equalsIgnoreCase("raw", this.objectedEncoding);
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
