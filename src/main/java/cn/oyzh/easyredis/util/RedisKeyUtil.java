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
import cn.oyzh.easyredis.redis.batch.RedisCountResult;
import cn.oyzh.easyredis.redis.batch.RedisDeleteResult;
import cn.oyzh.easyredis.redis.batch.RedisScanResult;
import cn.oyzh.easyredis.redis.batch.RedisScanSimpleResult;
import cn.oyzh.easyredis.redis.key.RedisHashKey;
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
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

//        // hylog
//        if (redisKey instanceof RedisHyLogKey) {
//            return "";
//        }

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
    public static RedisKey deserializeNode(RedisKeyType type, String value) {
        // string
        if (type == RedisKeyType.STRING) {
            RedisStringKey node = new RedisStringKey();
            node.value(value == null ? "" : value);
            return node;
        }

//        // hylog
//        if (StrUtil.equalsIgnoreCase(type, RedisKeyType.HYPERLOGLOG.toString())) {
//            RedisHyLogKey node = new RedisHyLogKey();
//            node.value(new byte[]{});
//            return node;
//        }

        // list
        if (type == RedisKeyType.LIST) {
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
        if (type == RedisKeyType.SET) {
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
        if (type == RedisKeyType.ZSET) {
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
        if (type == RedisKeyType.HASH) {
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
        if (type == RedisKeyType.STREAM) {
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
//        } else if (node instanceof RedisHyLogKey) {// hylog
//            client.pfadd(dbIndex, key, "");
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
     * @param node    redis键
     * @param dbIndex db索引
     * @param key     键
     * @param client  redis客户端
     */
    public static void keyValue(RedisKey node, Integer dbIndex, @NonNull String key, RedisClient client) {
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
        } else if (node instanceof RedisZSetKey zSetNode) { // zset
            List<String> value = client.zrange(dbIndex, key);
            List<Double> scores = client.zmscore_ext(dbIndex, key, ArrayUtil.toArray(value, String.class));
            zSetNode.valueOfScore(value, scores);
//        } else if (node instanceof RedisHyLogKey logLogNode) {// hylog
//            Long pfcount = client.pfcount(dbIndex, key);
//            byte[] value = client.get(dbIndex, key.getBytes());
//            logLogNode.value(value);
//            logLogNode.count(pfcount);
        } else if (node instanceof RedisStreamKey streamNode) {// stream
            streamNode.value(client.xrange(dbIndex, key));
        }
    }

    /**
     * 获取键对象信息
     *
     * @param node    redis键
     * @param dbIndex db索引
     * @param key     键
     * @param client  redis客户端
     */
    public static void keyObject(RedisKey node, Integer dbIndex, @NonNull String key, RedisClient client) {
        Long objectRefcount = client.objectRefcount(dbIndex, key);
        Long objectIdletime = client.objectIdletime(dbIndex, key);
        String objectEncoding = client.objectEncoding(dbIndex, key);
        node.objectIdletime(objectIdletime);
        node.objectRefcount(objectRefcount);
        node.objectedEncoding(objectEncoding);
    }

    /**
     * 扫描键
     *
     * @param dbIndex 都不索引
     * @param cursor  光标
     * @param params  参数
     * @param client  redis客户端
     * @return 扫描结果
     */
    public static RedisScanResult scanKeys(Integer dbIndex, String cursor, ScanParams params, RedisClient client) {
        // 开始时间
        long start = System.currentTimeMillis();
        // 扫描
        ScanResult<String> result = client.scan(dbIndex, cursor, params);
        RedisScanResult scanResult = new RedisScanResult();
        if (result == null) {
            return scanResult;
        }
        // 设置游标
        scanResult.setCursor(result.getCursor());
        // 获取游标结果
        List<String> keys = result.getResult();
        // 批量获取键类型
        List<RedisKeyType> types = keyType(dbIndex, keys, client);
        if (types == null) {
            throw new RuntimeException("获取键类型失败！");
        }
        // 结束时间
        long end = System.currentTimeMillis();
        // 加载耗时
        short loadTime = (short) (end - start);
        // 处理键
        List<RedisKey> redisKeys = new ArrayList<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            RedisKey redisKey = initKey(dbIndex, keys.get(i), types.get(i));
            redisKey.loadTime(loadTime);
            redisKeys.add(redisKey);
        }
        scanResult.setKeys(redisKeys);
        return scanResult;
    }

    /**
     * 扫描键，简单模式
     *
     * @param dbIndex 都不索引
     * @param cursor  光标
     * @param params  参数
     * @param client  redis客户端
     * @return 扫描结果
     */
    public static RedisScanSimpleResult scanKeysSimple(Integer dbIndex, String cursor, ScanParams params, RedisClient client) {
        // 扫描
        ScanResult<String> result = client.scan(dbIndex, cursor, params);
        RedisScanSimpleResult scanResult = new RedisScanSimpleResult();
        if (result == null) {
            return scanResult;
        }
        // 设置游标
        scanResult.setCursor(result.getCursor());
        // 获取游标结果
        List<String> keys = result.getResult();
        scanResult.setKeys(keys);
        return scanResult;
    }

    /**
     * 扫描键，简单限制
     *
     * @param dbIndex 都不索引
     * @param client  redis客户端
     * @param pattern 键模式
     * @param limit   最大限制
     * @return 键列表
     */
    public static List<String> scanKeys(Integer dbIndex, RedisClient client, String pattern, int limit) {
        ScanParams params = new ScanParams();
        params.count(limit);
        params.match(pattern);
        RedisScanSimpleResult result = scanKeysSimple(dbIndex, null, params, client);
        return result.getKeys();
    }

    /**
     * 统计键
     *
     * @param dbIndex 都不索引
     * @param cursor  光标
     * @param params  参数
     * @param client  redis客户端
     * @return 本次键数量
     */
    public static RedisCountResult countKeys(Integer dbIndex, String cursor, ScanParams params, RedisClient client) {
        // 扫描
        ScanResult<String> result = client.scan(dbIndex, cursor, params);
        RedisCountResult countResult = new RedisCountResult();
        if (result == null) {
            return countResult;
        }
        // 设置游标
        countResult.setCursor(result.getCursor());
        countResult.setCount(CollUtil.size(result.getResult()));
        return countResult;
    }

    /**
     * 删除键
     *
     * @param dbIndex 都不索引
     * @param cursor  光标
     * @param params  参数
     * @param client  redis客户端
     * @return 本次键数量
     */
    public static RedisDeleteResult deleteKeys(Integer dbIndex, String cursor, ScanParams params, RedisClient client) {
        // 扫描
        ScanResult<String> result = client.scan(dbIndex, cursor, params);
        RedisDeleteResult countResult = new RedisDeleteResult();
        if (result == null) {
            return countResult;
        }
        // 设置游标
        countResult.setCursor(result.getCursor());
        countResult.setCount(CollUtil.size(result.getResult()));
        client.del(dbIndex, result.getResult());
        return countResult;
    }

    /**
     * 获取所有键
     *
     * @param dbIndex db索引
     * @param pattern 键模式
     * @param client  redis客户端
     * @return 键列表
     */
    public static List<RedisKey> allKeys(Integer dbIndex, String pattern, RedisClient client) {
        // 开始时间
        long start = System.currentTimeMillis();
        // 获取键列表
        Set<String> keys = client.keys(dbIndex, pattern);
        // 批量获取键类型
        List<RedisKeyType> types = keyType(dbIndex, keys, client);
        if (types == null) {
            throw new RuntimeException(I18nResourceBundle.i18nString("base.get", "base.keyType", "base.fail"));
        }
        // 结束时间
        long end = System.currentTimeMillis();
        // 加载耗时
        short loadTime = (short) (end - start);
        // 处理键
        List<RedisKey> redisKeys = new ArrayList<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            RedisKey redisKey = initKey(dbIndex, CollUtil.get(keys, i), types.get(i));
            redisKey.loadTime(loadTime);
            redisKeys.add(redisKey);
        }
        return redisKeys;
    }

    /**
     * 获取键
     *
     * @param dbIndex   db索引
     * @param key       键
     * @param ttl       是否获取ttl
     * @param loadValue 是否加载值
     * @param client    redis客户端
     * @return redis键
     */
    public static RedisKey getKey(int dbIndex, @NonNull String key, boolean ttl, boolean loadValue, RedisClient client) {
        // 开始时间
        long start = System.currentTimeMillis();
        // 初始化键
        RedisKey redisKey = initKey(dbIndex, key, keyType(dbIndex, key, client));
        if (redisKey == null) {
            return null;
        }
        // ttl
        if (ttl) {
            redisKey.ttl(client.ttl(dbIndex, key));
        }
        // 值
        if (loadValue) {
            keyValue(redisKey, dbIndex, key, client);
        }
        // 结束时间
        long end = System.currentTimeMillis();
        // 加载耗时
        long loadTime = end - start;
        redisKey.loadTime((short) loadTime);
        return redisKey;
    }

    /**
     * 初始化键
     *
     * @param dbIndex db索引
     * @param key     键名称
     * @param type    键类型
     * @return redis键
     */
    public static RedisKey initKey(int dbIndex, @NonNull String key, RedisKeyType type) {
        // 创建键
        RedisKey redisKey = null;
        switch (type) {
            case RedisKeyType.STRING -> redisKey = new RedisStringKey();
            case RedisKeyType.LIST -> redisKey = new RedisListKey();
            case RedisKeyType.SET -> redisKey = new RedisSetKey();
            case RedisKeyType.ZSET -> redisKey = new RedisZSetKey();
            case RedisKeyType.HASH -> redisKey = new RedisHashKey();
            case RedisKeyType.STREAM -> redisKey = new RedisStreamKey();
            case null, default -> StaticLog.warn("type:{} is not support!", type);
        }
        // 处理键
        if (redisKey != null) {
            redisKey.key(key);
            redisKey.type(type);
            redisKey.dbIndex(dbIndex);
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
    public static RedisKeyType keyType(Integer dbIndex, String key, RedisClient client) {
        try {
            String type = client.type(dbIndex, key);
            return RedisKeyType.valueOfType(type);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取键类型
     *
     * @param dbIndex 数据库索引
     * @param keys    键
     * @param client  redis客户端
     * @return 结果
     */
    public static List<RedisKeyType> keyType(Integer dbIndex, Collection<String> keys, RedisClient client) {
        try {
            List<String> types = client.typeMulti(dbIndex, keys);
            if (types.size() == keys.size()) {
                List<RedisKeyType> list = new ArrayList<>();
                for (String type : types) {
                    list.add(RedisKeyType.valueOfType(type));
                }
                return list;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
