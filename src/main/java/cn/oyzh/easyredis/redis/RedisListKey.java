package cn.oyzh.easyredis.redis;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * redis list键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisListKey extends RedisRowKey<RedisListRow> {

    /**
     * 设置键数据
     *
     * @param value 键数据
     */
    public void value(List<String> value) {
        this.value = new ArrayList<>();
        if (CollUtil.isNotEmpty(value)) {
            for (int i = 0; i < value.size(); i++) {
                this.value.add(new RedisListRow(i, value.get(i)));
            }
        }
    }
}
