package cn.oyzh.easyredis.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyredis.domain.RedisFilter;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.redis.RedisHashRow;
import cn.oyzh.easyredis.redis.RedisKeyType;
import cn.oyzh.easyredis.redis.RedisScanResult;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.redis.key.RedisHyLogKey;
import cn.oyzh.easyredis.redis.key.RedisKey;
import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.redis.row.RedisListRow;
import cn.oyzh.easyredis.redis.row.RedisSetRow;
import cn.oyzh.easyredis.redis.row.RedisStreamRow;
import cn.oyzh.easyredis.redis.row.RedisZSetRow;
import cn.oyzh.fx.common.thread.ThreadUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * redis键工具类
 *
 * @author oyzh
 * @since 2023/06/30
 */
//@Slf4j
@UtilityClass
public class RedisKeyUtil {

    /**
     * 是否被过滤
     *
     * @param key     键名称
     * @param filters 过滤配置列表
     * @return 结果
     */
    public static boolean isFiltered(String key, List<RedisFilter> filters) {
        if (CollUtil.isEmpty(filters) || key == null) {
            return false;
        }
        // 匹配结果
        for (RedisFilter filter : filters) {
            // 未启用，不处理
            if (!filter.isEnable()) {
                continue;
            }
            // 模糊匹配
            if (filter.isPartMatch() && key.contains(filter.getKw())) {
                return true;
            }
            // 完全匹配
            if (key.equalsIgnoreCase(filter.getKw())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 序列化键
     *
     * @param redisKey redis键
     * @return 序列化内容
     */
    public static String serializeNode(RedisKey redisKey) {
        // string
        if (redisKey instanceof RedisStringKey stringNode) {
            return (String) stringNode.value();
        }

        // hylog
        if (redisKey instanceof RedisHyLogKey) {
            return "";
        }

        // list
        if (redisKey instanceof RedisListKey listNode) {
            if (CollUtil.isEmpty(listNode.value())) {
                return null;
            }
            List<Map<String, Object>> list = new ArrayList<>(listNode.value().size());
            for (RedisListRow row : listNode.value()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", row.getValue());
                list.add(map);
            }
            return JSONUtil.toJsonStr(list);
        }

        // set
        if (redisKey instanceof RedisSetKey setNode) {
            if (CollUtil.isEmpty(setNode.value())) {
                return null;
            }
            List<Map<String, Object>> list = new ArrayList<>(setNode.value().size());
            for (RedisSetRow row : setNode.value()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", row.getValue());
                list.add(map);
            }
            return JSONUtil.toJsonStr(list);
        }

        // zset
        if (redisKey instanceof RedisZSetKey zSetNode) {
            if (CollUtil.isEmpty(zSetNode.value())) {
                return null;
            }
            List<Map<String, Object>> list = new ArrayList<>(zSetNode.value().size());
            for (RedisZSetRow row : zSetNode.value()) {
                Map<String, Object> map = new HashMap<>();
                map.put("value", row.getValue());
                map.put("score", row.getScore());
                list.add(map);
            }
            return JSONUtil.toJsonStr(list);
        }

        // stream
        if (redisKey instanceof RedisStreamKey streamNode) {
            if (CollUtil.isEmpty(streamNode.value())) {
                return null;
            }
            List<Map<String, Object>> list = new ArrayList<>(streamNode.value().size());
            for (RedisStreamRow row : streamNode.value()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", row.getId());
                map.put("value", row.getValue());
                list.add(map);
            }
            return JSONUtil.toJsonStr(list);
        }

        // hash
        if (redisKey instanceof RedisHashKey hashNode) {
            if (CollUtil.isEmpty(hashNode.value())) {
                return null;
            }
            List<Map<String, Object>> list = new ArrayList<>(hashNode.value().size());
            for (RedisHashRow row : hashNode.value()) {
                Map<String, Object> map = new HashMap<>();
                map.put("field", row.getField());
                map.put("value", row.getValue());
                list.add(map);
            }
            return JSONUtil.toJsonStr(list);
        }
        return null;
    }

    /**
     * 反序列化键
     *
     * @param type  类型
     * @param value 值
     * @return redis键
     */
    public static RedisKey deserializeNode(String type, String value) {
        // string
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.STRING.toString())) {
            RedisStringKey node = new RedisStringKey();
            node.value(value == null ? "" : value);
            return node;
        }

        // hylog
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.HYPERLOGLOG.toString())) {
            RedisHyLogKey node = new RedisHyLogKey();
            node.value(new byte[]{});
            return node;
        }

        // list
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.LIST.toString())) {
            RedisListKey node = new RedisListKey();
            List<String> list = new ArrayList<>();
            if (StrUtil.isNotBlank(value)) {
                JSONArray array = JSONUtil.parseArray(value);
                for (int i = 0; i < array.size(); i++) {
                    list.add(array.getJSONObject(i).getStr("value"));
                }
            }
            node.value(list);
            return node;
        }

        // set
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.SET.toString())) {
            RedisSetKey node = new RedisSetKey();
            Set<String> list = new HashSet<>();
            if (StrUtil.isNotBlank(value)) {
                JSONArray array = JSONUtil.parseArray(value);
                for (int i = 0; i < array.size(); i++) {
                    list.add(array.getJSONObject(i).getStr("value"));
                }
            }
            node.value(list);
            return node;
        }

        // zset
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.ZSET.toString())) {
            RedisZSetKey node = new RedisZSetKey();
            List<String> list1 = new ArrayList<>();
            List<Double> list2 = new ArrayList<>();
            if (StrUtil.isNotBlank(value)) {
                JSONArray array = JSONUtil.parseArray(value);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    list1.add(object.getStr("value"));
                    list2.add(object.getDouble("score"));
                }
            }
            node.valueOfScore(list1, list2);
            return node;
        }

        // hash
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.HASH.toString())) {
            RedisHashKey node = new RedisHashKey();
            Map<String, String> map = new HashMap<>();
            if (StrUtil.isNotBlank(value)) {
                JSONArray array = JSONUtil.parseArray(value);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    map.put(object.getStr("field"), object.getStr("value"));
                }
            }
            node.value(map);
            return node;
        }

        // stream
        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.STREAM.toString())) {
            RedisStreamKey node = new RedisStreamKey();
            List<StreamEntry> list = new ArrayList<>();
            if (StrUtil.isNotBlank(value)) {
                JSONArray array = JSONUtil.parseArray(value);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    StreamEntryID id = new StreamEntryID(object.getStr("id"));
                    String fields = object.getStr("value");
                    Map<String, String> fieldMap;
                    if (StrUtil.isNotBlank(fields)) {
                        fieldMap = new HashMap<>();
                    } else {
                        fieldMap = JSONUtil.toBean(fields, HashMap.class);
                    }
                    StreamEntry entry = new StreamEntry(id, fieldMap);
                    list.add(entry);
                }
            }
            node.value(list);
            return node;
        }
        return null;
    }

    /**
     * 创建键
     *
     * @param node    redis键
     * @param dbIndex db索引
     * @param client  redis客户端
     */
    public static void createNode(RedisKey node, Integer dbIndex, RedisClient client) {
        if (node == null || client == null) {
            return;
        }
        String key = node.key();
        // string
        if (node instanceof RedisStringKey stringNode) {
            client.set(dbIndex, key, (String) stringNode.value());
        } else if (node instanceof RedisHyLogKey) {// hylog
            client.pfadd(dbIndex, key, "");
        } else if (node instanceof RedisListKey listNode) {// list
            String[] arr;
            if (CollUtil.isEmpty(listNode.value())) {
                arr = new String[]{""};
            } else {
                List<String> list = listNode.value().parallelStream().map(RedisListRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(list, String.class);
            }
            client.lpush(dbIndex, key, arr);
        } else if (node instanceof RedisSetKey setNode) {// set
            String[] arr;
            if (CollUtil.isEmpty(setNode.value())) {
                arr = new String[]{""};
            } else {
                List<String> list = setNode.value().parallelStream().map(RedisSetRow::getValue).collect(Collectors.toList());
                arr = ArrayUtil.toArray(list, String.class);
            }
            client.sadd(dbIndex, key, arr);
        } else if (node instanceof RedisZSetKey zSetNode) {// zset
            Map<String, Double> scoreMembers;
            if (CollUtil.isEmpty(zSetNode.value())) {
                scoreMembers = Collections.emptyMap();
            } else {
                scoreMembers = new HashMap<>();
                for (RedisZSetRow row : zSetNode.value()) {
                    scoreMembers.put(row.getValue(), row.getScore());
                }
            }
            client.zadd(dbIndex, key, scoreMembers);
        } else if (node instanceof RedisHashKey hashNode) {// hash
            Map<String, String> hash;
            if (CollUtil.isEmpty(hashNode.value())) {
                hash = Collections.emptyMap();
            } else {
                hash = new HashMap<>();
                for (RedisHashRow row : hashNode.value()) {
                    hash.put(row.getField(), row.getValue());
                }
            }
            client.hmset(dbIndex, key, hash);
        } else if (node instanceof RedisStreamKey streamNode) {// stream
            if (CollUtil.isNotEmpty(streamNode.value())) {
                for (RedisStreamRow row : streamNode.value()) {
                    client.xadd(dbIndex, key, row.getEntry().getID(), row.getEntry().getFields());
                }
            }
        }
    }

    /**
     * 获取键值
     *
     * @param node    redis节点
     * @param dbIndex db索引
     * @param key     键
     * @param client  redis客户端
     */
    public static void getNodeValue(RedisKey node, Integer dbIndex, @NonNull String key, RedisClient client) {
        // string
        if (node instanceof RedisStringKey stringNode) {
            String value = client.get(dbIndex, key);
            stringNode.value(value);
        } else if (node instanceof RedisListKey listKey) {// list
            List<String> value = client.lrange(dbIndex, key);
            listKey.value(value);
        } else if (node instanceof RedisHashKey hashNode) {// hash
            Map<String, String> value = client.hgetAll(dbIndex, key);
            hashNode.value(value);
        } else if (node instanceof RedisSetKey setNode) {// set
            Set<String> value = client.smembers(dbIndex, key);
            setNode.value(value);
            setNode.value(value);
        } else if (node instanceof RedisZSetKey zSetNode) { // zset
            List<String> value = client.zrange(dbIndex, key);
            List<Double> scores = client.zmscore_ext(dbIndex, key, ArrayUtil.toArray(value, String.class));
            zSetNode.valueOfScore(value, scores);
        } else if (node instanceof RedisHyLogKey logLogNode) {// hylog
            Long pfcount = client.pfcount(dbIndex, key);
            byte[] value = client.get(dbIndex, key.getBytes());
            logLogNode.value(value);
            logLogNode.count(pfcount);
        } else if (node instanceof RedisStreamKey streamNode) {// stream
            streamNode.value(client.xrange(dbIndex, key));
        }
    }

    /**
     * 获取键对象信息
     *
     * @param node    redis节点
     * @param dbIndex db索引
     * @param key     键
     * @param client  redis客户端
     */
    public static void getNodeObject(RedisKey node, Integer dbIndex, @NonNull String key, RedisClient client) {
        Long objectRefcount = client.objectRefcount(dbIndex, key);
        Long objectIdletime = client.objectIdletime(dbIndex, key);
        String objectEncoding = client.objectEncoding(dbIndex, key);
        node.objectIdletime(objectIdletime);
        node.objectRefcount(objectRefcount);
        node.objectedEncoding(objectEncoding);
    }

    /**
     * 扫描节点
     *
     * @param dbIndex 都不索引
     * @param cursor  光标
     * @param params  参数
     * @param client  redis客户端
     * @return 扫描结果
     */
    public static RedisScanResult scanNodes(Integer dbIndex, String cursor, ScanParams params, RedisClient client) {
        return scanNodes(dbIndex, cursor, params, false, client);
    }

    /**
     * 扫描节点
     *
     * @param dbIndex   都不索引
     * @param cursor    光标
     * @param params    参数
     * @param loadValue 是否加载值
     * @param client    redis客户端
     * @return 扫描结果
     */
    public static RedisScanResult scanNodes(Integer dbIndex, String cursor, ScanParams params, boolean loadValue, RedisClient client) {
        ScanResult<String> result = client.scan(dbIndex, cursor, params);
        RedisScanResult scanResult = new RedisScanResult();
        if (result == null) {
            return scanResult;
        }
        scanResult.setCursor(result.getCursor());
        List<Callable<RedisKey>> tasks = new ArrayList<>(result.getResult().size());
        for (String key : result.getResult()) {
            tasks.add(() -> getNode(dbIndex, key, false, loadValue, client));
        }
        List<RedisKey> nodes = ThreadUtil.invoke(tasks);
        scanResult.setKeys(nodes);
        return scanResult;
    }

    /**
     * 获取所有节点
     *
     * @param dbIndex   db索引
     * @param pattern   键模式
     * @param loadValue 是否加载值
     * @param client    redis客户端
     * @return 节点列表
     */
    public static List<RedisKey> allNodes(Integer dbIndex, String pattern, boolean loadValue, RedisClient client) {
        Set<String> keys = client.keys(dbIndex, pattern);
        List<RedisKey> nodes = new ArrayList<>(keys.size());
        for (String key : keys) {
            RedisKey node = getNode(dbIndex, key, false, loadValue, client);
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * 获取节点
     *
     * @param dbIndex db索引
     * @param key     键
     * @param client  redis客户端
     * @return redis节点
     */
    public static RedisKey getNode(int dbIndex, @NonNull String key, RedisClient client) {
        return getNode(dbIndex, key, false, false, client);
    }

    /**
     * 获取节点
     *
     * @param dbIndex   db索引
     * @param key       键
     * @param ttl       是否获取ttl
     * @param loadValue 加载值
     * @param client    redis客户端
     * @return redis节点
     */
    public static RedisKey getNode(int dbIndex, @NonNull String key, boolean ttl, boolean loadValue, RedisClient client) {
        long start = System.currentTimeMillis();
        // 任务
        List<Runnable> tasks = new ArrayList<>(2);
        // ttl
        AtomicReference<Long> ttlRef;
        // 类型
        AtomicReference<String> typeRef = new AtomicReference<>();
        // 类型任务
        tasks.add(() -> typeRef.set(getKeyType(dbIndex, key, client)));
        // ttl任务
        if (ttl) {
            ttlRef = new AtomicReference<>();
            tasks.add(() -> ttlRef.set(client.ttl(dbIndex, key)));
        } else {
            ttlRef = null;
        }
        // 执行任务
        if (tasks.size() == 1) {
            tasks.getFirst().run();
        } else {
            ThreadUtil.submitVirtual(tasks);
        }
        // 创建键
        RedisKey redisKey = null;
        switch (typeRef.get()) {
            case "string" -> redisKey = new RedisStringKey();
            case "hyLog" -> redisKey = new RedisHyLogKey();
            case "list" -> redisKey = new RedisListKey();
            case "set" -> redisKey = new RedisSetKey();
            case "zset" -> redisKey = new RedisZSetKey();
            case "hash" -> redisKey = new RedisHashKey();
            case "stream" -> redisKey = new RedisStreamKey();
            case null, default -> StaticLog.warn("type:{} is not support!", typeRef.get());
        }
        // 处理键
        if (redisKey != null) {
            redisKey.key(key);
            redisKey.dbIndex(dbIndex);
            redisKey.type(typeRef.get());
            if (ttlRef != null) {
                redisKey.ttl(ttlRef.get());
            }
            // 加载值
            if (loadValue) {
                getNodeValue(redisKey, dbIndex, key, client);
            }
            long end = System.currentTimeMillis();
            long loadTime = end - start;
            redisKey.loadTime((short) loadTime);
        }
        return redisKey;
    }

    /**
     * 获取键类型
     *
     * @param dbIndex 数据库索引
     * @param key     键
     * @param client  redis客户端
     * @return 结果
     */
    public static String getKeyType(Integer dbIndex, String key, RedisClient client) {
        try {
            String type = client.type(dbIndex, key);
            if ("string".equals(type)) {
                try {
                    if (client.pfcount(dbIndex, key) > 0) {
                        return "hyLog";
                    }
                } catch (Exception ex) {
                    if (StrUtil.containsAny(ex.getMessage(), "WRONGTYPE Key is not a valid HyperLogLog string value")) {
                        return "string";
                    }
                    ex.printStackTrace();
                }
            }
            return type;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
