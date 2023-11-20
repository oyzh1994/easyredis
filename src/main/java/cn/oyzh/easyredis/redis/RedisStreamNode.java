package cn.oyzh.easyredis.redis;

import cn.hutool.core.collection.CollUtil;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisStreamNode extends RedisRowNode<RedisStreamRow> {

    /**
     * 设置数据
     *
     * @param value 数据
     */
    public void value(List<StreamEntry> value) {
        this.valueInitialized = true;
        this.value = new ArrayList<>();
        if (CollUtil.isNotEmpty(value)) {
            for (StreamEntry entry : value) {
                this.value.add(new RedisStreamRow(entry));
            }
        }
    }

    @Override
    public List<Map<String, Object>> getSerializableValue() {
        if (CollUtil.isEmpty(this.value)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>(this.value.size());
        for (RedisStreamRow row : this.value) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", row.getId());
            map.put("value", row.getValue());
            list.add(map);
        }
        return list;
    }
}
