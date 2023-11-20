package cn.oyzh.easyredis.redis.node;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.redis.RedisRowNode;
import cn.oyzh.easyredis.redis.row.RedisListRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisListNode extends RedisRowNode<RedisListRow> {

    /**
     * 设置节点数据
     *
     * @param value 节点数据
     */
    public void value(List<String> value) {
        this.valueInitialized = true;
        this.value = new ArrayList<>();
        if (CollUtil.isNotEmpty(value)) {
            for (int i = 0; i < value.size(); i++) {
                this.value.add(new RedisListRow(i, value.get(i)));
            }
        }
    }

    @Override
    public List<Map<String, Object>> getSerializableValue() {
        if (CollUtil.isEmpty(this.value)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>(this.value.size());
        for (RedisListRow row : this.value) {
            Map<String, Object> map = new HashMap<>();
            map.put("value", row.getValue());
            list.add(map);
        }
        return list;
    }
}
