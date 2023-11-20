package cn.oyzh.easyredis.redis.key;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.redis.RedisRowKey;

import java.util.ArrayList;
import java.util.Map;

/**
 * redis hash键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisHashKey extends RedisRowKey<RedisHashRow> {

    /**
     * 设置键数据
     *
     * @param value 键数据
     */
    public void value(Map<String, String> value) {
        this.value = new ArrayList<>();
        if (CollUtil.isNotEmpty(value)) {
            for (Map.Entry<String, String> entry : value.entrySet()) {
                this.value.add(new RedisHashRow(entry.getKey(), entry.getValue()));
            }
        }
    }
}
