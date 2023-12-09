//package cn.oyzh.easyredis.redis.node;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.oyzh.easyredis.redis.RedisRowNode;
//import cn.oyzh.easyredis.redis.row.RedisZSetRow;
//import redis.clients.jedis.GeoCoordinate;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author oyzh
// * @since 2023/6/16
// */
//public class RedisZSetNode extends RedisRowNode<RedisZSetRow> {
//
//    /**
//     * 设置数据
//     *
//     * @param value  数据
//     * @param scores 分数
//     */
//    public void valueOfScore(List<String> value, List<Double> scores) {
//        this.valueInitialized = true;
//        if (this.value == null) {
//            this.value = new ArrayList<>();
//        }
//        if (!this.value.isEmpty()) {
//            this.value.clear();
//        }
//        if (CollUtil.isNotEmpty(value)) {
//            int i = 0;
//            for (String member : value) {
//                this.value.add(new RedisZSetRow(member, scores.get(i++)));
//            }
//        }
//    }
//
//    /**
//     * 设置数据
//     *
//     * @param value       数据
//     * @param coordinates 坐标
//     */
//    public void valueOfCoordinate(List<String> value, List<GeoCoordinate> coordinates) {
//        this.valueInitialized = true;
//        if (this.value == null) {
//            this.value = new ArrayList<>();
//        }
//        if (!this.value.isEmpty()) {
//            this.value.clear();
//        }
//        if (CollUtil.isNotEmpty(value)) {
//            int i = 0;
//            for (String member : value) {
//                this.value.add(new RedisZSetRow(member, coordinates.get(i++)));
//            }
//        }
//    }
//
//    @Override
//    public List<Map<String, Object>> getSerializableValue() {
//        if (CollUtil.isEmpty(this.value)) {
//            return Collections.emptyList();
//        }
//        List<Map<String, Object>> list = new ArrayList<>(this.value.size());
//        for (RedisZSetRow row : this.value) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("value", row.getValue());
//            map.put("score", row.getScore());
//            list.add(map);
//        }
//        return list;
//    }
//}
