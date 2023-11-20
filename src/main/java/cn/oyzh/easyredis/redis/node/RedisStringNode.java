package cn.oyzh.easyredis.redis.node;

import cn.oyzh.easyredis.redis.RedisNode;
import cn.oyzh.fx.common.util.TextUtil;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.charset.Charset;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisStringNode extends RedisNode {

    /**
     * 节点值
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private String value;

    /**
     * 设置节点数据
     *
     * @param value 节点数据
     */
    public void value(String value) {
        this.value = value;
        this.valueInitialized = true;
    }

    /**
     * 节点值字符串
     *
     * @param charset 字符集
     * @return 节点值字符串
     */
    public String valueStr(String charset) {
        return this.valueStr(TextUtil.getCharset(charset));
    }

    /**
     * 节点值字符串
     *
     * @param charset 字符集
     * @return 节点值字符串
     */
    public String valueStr(Charset charset) {
        if (this.value == null) {
            return null;
        }
        if (charset == null) {
            return this.value;
        }
        return new String(this.value.getBytes(), charset);
    }
}
