package cn.oyzh.easyredis.redis.key;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyredis.redis.RedisRowKey;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import redis.clients.jedis.GeoCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * redis zset键
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisZSetKey extends RedisRowKey<RedisZSetRow> {

    /**
     * 设置数据
     *
     * @param value  数据
     * @param scores 分数
     */
    public void valueOfScore(List<String> value, List<Double> scores) {
        if (this.value == null) {
            this.value = new ArrayList<>();
        } else if (!this.value.isEmpty()) {
            this.value.clear();
        }
        if (CollUtil.isNotEmpty(value)) {
            int i = 0;
            for (String member : value) {
                this.value.add(new RedisZSetRow(member, scores.get(i++)));
            }
        }
    }

    /**
     * 设置数据
     *
     * @param value       数据
     * @param coordinates 坐标
     */
    public void valueOfCoordinate(List<String> value, List<GeoCoordinate> coordinates) {
        if (this.value == null) {
            this.value = new ArrayList<>();
        } else if (!this.value.isEmpty()) {
            this.value.clear();
        }
        if (CollUtil.isNotEmpty(value)) {
            int i = 0;
            for (String member : value) {
                this.value.add(new RedisZSetRow(member, coordinates.get(i++)));
            }
        }
    }
}
