package cn.oyzh.easyredis.redis.key;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyredis.redis.RedisRowKey;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * redis stream键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisStreamKey extends RedisRowKey<RedisStreamRow> {

    /**
     * 设置数据
     *
     * @param value 数据
     */
    public void value(List<StreamEntry> value) {
        this.value = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(value)) {
            for (StreamEntry entry : value) {
                this.value.add(new RedisStreamRow(entry));
            }
        }
    }
}
