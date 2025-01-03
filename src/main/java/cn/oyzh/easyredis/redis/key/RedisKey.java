package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.redis.RedisKeyType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.resps.StreamEntry;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis键
 *
 * @author oyzh
 * @since 2023/6/16
 */

public class RedisKey implements Comparable<RedisKey>, ObjectCopier<RedisKey> {

    /**
     * db索引
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private int dbIndex;

    /**
     * 加载耗时
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private short loadTime;

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
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private Long objectIdletime;

    /**
     * 引用数量
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private Long objectRefcount;

    /**
     * 编码值
     */
    @Setter
    @Getter
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

    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private RedisKeyValue<?> value;

    public void valueOfSet(Set<String> members) {
        this.value(RedisSetValue.valueOf(members));
    }

    public void valueOfZSet(List<String> members, List<Double> scores) {
        this.value(RedisZSetValue.valueOf(members, scores));
    }

    public void valueOfCoordinates(List<String> members, List<GeoCoordinate> coordinates) {
        this.value(RedisZSetValue.valueOfCoordinates(members, coordinates));
    }

    public void valueOfHash(Map<String, String> values) {
        this.value(RedisHashValue.valueOf(values));
    }

    public void valueOfList(List<String> elements) {
        this.value(RedisListValue.valueOf(elements));
    }

    public void valueOfStream(List<StreamEntry> entries) {
        this.value(RedisStreamValue.valueOf(entries));
    }

    public void valueOfString(String value) {
        this.value(RedisStringValue.valueOf(value));
    }

    public void valueOfString(byte[] value) {
        this.value(RedisStringValue.valueOf(value));
    }

    public RedisSetValue asSetValue() {
        return (RedisSetValue) this.value();
    }

    public RedisZSetValue asZSetValue() {
        return (RedisZSetValue) this.value();
    }

    public RedisListValue asListValue() {
        return (RedisListValue) this.value();
    }

    public RedisHashValue asHashValue() {
        return (RedisHashValue) this.value();
    }

    public RedisStringValue asStringValue() {
        RedisKeyValue<?> value = this.value();
        if (value == null) {
            value = new RedisStringValue();
            this.value(value);
        }
        return (RedisStringValue) value;
    }

    public RedisStreamValue asStreamValue() {
        return (RedisStreamValue) this.value();
    }

    public String typeName() {
        return this.type.name();
    }

    @Override
    public void copy(RedisKey t1) {
        this.key(t1.key());
        this.ttl(t1.ttl());
        this.type(t1.type());
        this.value(t1.value());
        this.dbIndex(t1.dbIndex());
        this.objectIdletime(t1.objectIdletime());
        this.objectRefcount(t1.objectRefcount());
        this.objectedEncoding(t1.objectedEncoding());
    }
}
