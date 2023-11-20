package cn.oyzh.easyredis.redis;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisHashNode extends RedisRowNode<RedisHashRow> {

    /**
     * 设置节点数据
     *
     * @param value 节点数据
     */
    public void value(Map<String, String> value) {
        this.valueInitialized = true;
        this.value = new ArrayList<>();
        if (CollUtil.isNotEmpty(value)) {
            for (Map.Entry<String, String> entry : value.entrySet()) {
                this.value.add(new RedisHashRow(entry.getKey(), entry.getValue()));
            }
        }
    }

    @Override
    public List<Map<String, Object>> getSerializableValue() {
        if (CollUtil.isEmpty(this.value)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>(this.value.size());
        for (RedisHashRow row : this.value) {
            Map<String, Object> map = new HashMap<>();
            map.put("field", row.getField());
            map.put("value", row.getValue());
            list.add(map);
        }
        return list;
    }
}
