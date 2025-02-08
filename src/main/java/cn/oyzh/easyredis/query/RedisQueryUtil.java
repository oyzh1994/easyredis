package cn.oyzh.easyredis.query;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.experimental.UtilityClass;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static redis.clients.jedis.Protocol.Command.*;

/**
 * @author oyzh
 * @since 2025/01/21
 */
@UtilityClass
public class RedisQueryUtil {

    /**
     * 关键字
     */
    private static final Set<String> KEYWORDS = new HashSet<>();

    /**
     * 参数
     */
    private static final Set<String> PARAMS = new HashSet<>();

    /**
     * 键
     */
    private static final Set<String> KEYS = new HashSet<>();

    static {
        // 关键字
        for (Protocol.Command command : Protocol.Command.values()) {
            KEYWORDS.add(command.toString());
        }
        for (Protocol.Keyword keyword : Protocol.Keyword.values()) {
            KEYWORDS.add(keyword.toString());
        }
        for (Protocol.ClusterKeyword keyword : Protocol.ClusterKeyword.values()) {
            KEYWORDS.add(keyword.toString());
        }
        for (Protocol.ResponseKeyword keyword : Protocol.ResponseKeyword.values()) {
            KEYWORDS.add(keyword.toString());
        }
        for (Protocol.SentinelKeyword keyword : Protocol.SentinelKeyword.values()) {
            KEYWORDS.add(keyword.toString());
        }
        // 参数
        for (Protocol.Keyword keyword : Protocol.Keyword.values()) {
            PARAMS.add(keyword.toString());
        }
        for (Protocol.ClusterKeyword keyword : Protocol.ClusterKeyword.values()) {
            PARAMS.add(keyword.toString());
        }
        for (Protocol.ResponseKeyword keyword : Protocol.ResponseKeyword.values()) {
            PARAMS.add(keyword.toString());
        }
        for (Protocol.SentinelKeyword keyword : Protocol.SentinelKeyword.values()) {
            PARAMS.add(keyword.toString());
        }
    }

    public static Set<String> getKeywords() {
        return KEYWORDS;
    }

    public static Set<String> getParams() {
        return PARAMS;
    }

    public static Set<String> getKeys() {
        return KEYS;
    }

    public static void setKeys(Collection<String> keys) {
        KEYS.clear();
        if (keys != null) {
            KEYS.addAll(keys);
        }
    }

    public static double clacCorr(String str, String text) {
        str = str.toUpperCase();
        text = text.toUpperCase();
        if (!str.contains(text) && !text.contains(str)) {
            return 0.d;
        }
        double corr = StringUtil.similarity(str, text);
        if (str.startsWith(text)) {
            corr += 0.3;
        } else if (str.contains(text)) {
            corr += 0.2;
        } else if (str.endsWith(text)) {
            corr += 0.1;
        }
        return corr;
    }

    /**
     * 初始化提示词
     *
     * @param token   提示词
     * @param minCorr 最低相关度
     * @return 结果
     */
    public static List<RedisQueryPromptItem> initPrompts(RedisQueryToken token, float minCorr) {
        if (token == null) {
            return Collections.emptyList();
        }
        // 当前提示词
        String text = token.getContent().toUpperCase();
        // 提示词列表
        final List<RedisQueryPromptItem> items = new CopyOnWriteArrayList<>();
        // 任务列表
        List<Runnable> tasks = new ArrayList<>();
        // 关键字
        if (token.isPossibilityKeyword()) {
            tasks.add(() -> getKeywords().parallelStream().forEach(keyword -> {
                // 计算相关度
                double corr = clacCorr(keyword, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 1);
                    item.setContent(keyword);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 参数
        if (token.isPossibilityParam()) {
            tasks.add(() -> getParams().parallelStream().forEach(param -> {
                // 计算相关度
                double corr = clacCorr(param, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 2);
                    item.setContent(param);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 键
        if (token.isPossibilityKey()) {
            tasks.add(() -> getKeys().parallelStream().forEach(key -> {
                // 计算相关度
                double corr = clacCorr(key, text);
                if (corr > minCorr) {
                    RedisQueryPromptItem item = new RedisQueryPromptItem();
                    item.setType((byte) 3);
                    item.setContent(key);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 执行任务
        ThreadUtil.submitVirtual(tasks);
        // 根据相关度排序
        return items.parallelStream()
                .sorted(Comparator.comparingDouble(RedisQueryPromptItem::getCorrelation))
                .collect(Collectors.toList())
                .reversed();
    }


    public static List<Protocol.Command> keyCommands() {
        List<Protocol.Command> commands = new ArrayList<>();
//        commands.add(Protocol.Command.APPEND);
//
//        commands.add(Protocol.Command.BLMOVE);
//        commands.add(Protocol.Command.BITCOUNT);
//        commands.add(Protocol.Command.BITFIELD);
//        commands.add(Protocol.Command.BITOP);
//        commands.add(Protocol.Command.BITPOS);
//        commands.add(Protocol.Command.BLMPOP);
//        commands.add(Protocol.Command.BLPOP);
//        commands.add(Protocol.Command.BRPOP);
//        commands.add(Protocol.Command.BRPOPLPUSH);
//        commands.add(Protocol.Command.BZMPOP);
//        commands.add(Protocol.Command.BZPOPMAX);
//        commands.add(Protocol.Command.BZPOPMIN);
//
//        commands.add(Protocol.Command.COPY);
//
//        commands.add(Protocol.Command.DECR);
//        commands.add(Protocol.Command.DEL);
//        commands.add(Protocol.Command.DISCARD);
//        commands.add(Protocol.Command.DUMP);
//
//        commands.add(Protocol.Command.EXPIRE);
//        commands.add(Protocol.Command.EXISTS);
//        commands.add(Protocol.Command.EXPIREAT);
//
//        commands.add(Protocol.Command.FLUSHDB);
//        commands.add(Protocol.Command.FLUSHALL);
//
//        commands.add(Protocol.Command.GEOADD);
//        commands.add(Protocol.Command.GEODIST);
//        commands.add(Protocol.Command.GEORADIUS);
//        commands.add(Protocol.Command.GEORADIUS_RO);
//        commands.add(Protocol.Command.GEOHASH);
//        commands.add(Protocol.Command.GEOPOS);
//        commands.add(Protocol.Command.GEOSEARCH);
//        commands.add(Protocol.Command.GEOSEARCHSTORE);
//        commands.add(Protocol.Command.GEORADIUSBYMEMBER);
//        commands.add(Protocol.Command.GEORADIUSBYMEMBER_RO);
//
//        commands.add(Protocol.Command.GET);
//        commands.add(Protocol.Command.GETBIT);
//        commands.add(Protocol.Command.GETDEL);
//        commands.add(Protocol.Command.GETEX);
//        commands.add(Protocol.Command.GETSET);
//        commands.add(Protocol.Command.GETRANGE);
//
//        commands.add(Protocol.Command.HDEL);
//        commands.add(Protocol.Command.HEXPIRE);
//        commands.add(Protocol.Command.HEXPIRETIME);
//        commands.add(Protocol.Command.HEXISTS);
//        commands.add(Protocol.Command.HPEXPIRETIME);
//        commands.add(Protocol.Command.HGET);
//        commands.add(Protocol.Command.HGETALL);
//        commands.add(Protocol.Command.HINCRBY);
//        commands.add(Protocol.Command.HINCRBYFLOAT);
//        commands.add(Protocol.Command.HKEYS);
//        commands.add(Protocol.Command.HLEN);
//        commands.add(Protocol.Command.HSET);
//        commands.add(Protocol.Command.HSCAN);
//        commands.add(Protocol.Command.HSETNX);
//        commands.add(Protocol.Command.HSTRLEN);
//
//        commands.add(Protocol.Command.INCR);
//        commands.add(Protocol.Command.INCRBY);
//        commands.add(Protocol.Command.INCRBYFLOAT);
//
//        commands.add(Protocol.Command.LINDEX);
//        commands.add(Protocol.Command.LRANGE);
//        commands.add(Protocol.Command.LREM);
//        commands.add(Protocol.Command.LMOVE);
//        commands.add(Protocol.Command.LLEN);
//        commands.add(Protocol.Command.LINSERT);
//        commands.add(Protocol.Command.LMPOP);
//        commands.add(Protocol.Command.LPOP);
//        commands.add(Protocol.Command.LPOS);
//        commands.add(Protocol.Command.LPUSH);
//        commands.add(Protocol.Command.LPUSHX);
//        commands.add(Protocol.Command.LSET);
//
//        commands.add(Protocol.Command.MOVE);
//        commands.add(Protocol.Command.MGET);
//        commands.add(Protocol.Command.MSETNX);
//
//        commands.add(Protocol.Command.OBJECT);
//
//        commands.add(Protocol.Command.PEXPIRE);
//        commands.add(Protocol.Command.PFMERGE);
//        commands.add(Protocol.Command.PEXPIRETIME);
//        commands.add(Protocol.Command.PERSIST);
//        commands.add(Protocol.Command.PEXPIREAT);
//        commands.add(Protocol.Command.PFMERGE);
//        commands.add(Protocol.Command.PFADD);
//        commands.add(Protocol.Command.PFCOUNT);
//        commands.add(Protocol.Command.PSETEX);
//        commands.add(Protocol.Command.PTTL);
//
//        commands.add(Protocol.Command.RENAME);
//        commands.add(Protocol.Command.RENAMENX);
//        commands.add(Protocol.Command.RPOP);
//        commands.add(Protocol.Command.RPOPLPUSH);
//        commands.add(Protocol.Command.RPUSH);
//        commands.add(Protocol.Command.RPUSHX);
//
//        commands.add(Protocol.Command.SET);
//        commands.add(Protocol.Command.SADD);
//        commands.add(Protocol.Command.SDIFF);
//        commands.add(Protocol.Command.SDIFFSTORE);
//        commands.add(Protocol.Command.SETRANGE);
//        commands.add(Protocol.Command.SETRANGE);
//        commands.add(Protocol.Command.SETEX);
//        commands.add(Protocol.Command.SETBIT);
//        commands.add(Protocol.Command.SETNX);
//        commands.add(Protocol.Command.SINTER);
//        commands.add(Protocol.Command.SINTERCARD);
//        commands.add(Protocol.Command.SINTERSTORE);
//        commands.add(Protocol.Command.STRLEN);
//        commands.add(Protocol.Command.SSCAN);
//        commands.add(Protocol.Command.SMOVE);
//        commands.add(Protocol.Command.SUNION);
//        commands.add(Protocol.Command.SUNIONSTORE);
//        commands.add(Protocol.Command.SCARD);
//        commands.add(Protocol.Command.SISMEMBER);
//
//        commands.add(Protocol.Command.TTL);
//        commands.add(Protocol.Command.TYPE);
//
//        commands.add(Protocol.Command.UNLINK);
//
//        commands.add(Protocol.Command.XRANGE);
//        commands.add(Protocol.Command.XACK);
//        commands.add(Protocol.Command.XADD);
//        commands.add(Protocol.Command.XRANGE);
//        commands.add(Protocol.Command.XREVRANGE);
//        commands.add(Protocol.Command.XREADGROUP);
//        commands.add(Protocol.Command.XCLAIM);
//        commands.add(Protocol.Command.XDEL);
//        commands.add(Protocol.Command.XGROUP);
//        commands.add(XINFO);
//        commands.add(Protocol.Command.XTRIM);
//
//        commands.add(Protocol.Command.ZDIFF);
//        commands.add(Protocol.Command.ZDIFFSTORE);
//        commands.add(Protocol.Command.ZINTER);
//        commands.add(Protocol.Command.ZINTERSTORE);
//        commands.add(Protocol.Command.ZINCRBY);
//        commands.add(Protocol.Command.ZINTERCARD);
//        commands.add(Protocol.Command.ZMSCORE);
//        commands.add(Protocol.Command.ZRANGE);
//        commands.add(Protocol.Command.ZRANGEBYSCORE);
//        commands.add(Protocol.Command.ZREM);
//        commands.add(Protocol.Command.ZREVRANGE);
//        commands.add(Protocol.Command.ZRANK);
//        commands.add(Protocol.Command.ZREVRANK);

        // key
        Protocol.Command[] keyCommands = new Protocol.Command[]{
                EXPIRE, EXPIREAT, EXPIRETIME, PEXPIRE, PEXPIREAT, PEXPIRETIME, TTL, PTTL
        };
        // string
        Protocol.Command[] stringCommands = new Protocol.Command[]{
                MULTI, DISCARD, EXEC, WATCH, UNWATCH, SORT, SORT_RO, INFO, SHUTDOWN, MONITOR, CONFIG, LCS, //
                GETSET, MGET, SETNX, SETEX, PSETEX, MSET, MSETNX, DECR, DECRBY, INCR, INCRBY, INCRBYFLOAT,
                STRLEN, APPEND, SUBSTR
        };
        // bit
        Protocol.Command[] bitCommands = new Protocol.Command[]{
                SETBIT, GETBIT, BITPOS, SETRANGE, GETRANGE, BITCOUNT, BITOP, BITFIELD, BITFIELD_RO
        };
        // hash
        Protocol.Command[] hashCommands = new Protocol.Command[]{
                HSET, HGET, HSETNX, HMSET, HMGET, HINCRBY, HEXISTS, HDEL, HLEN, HKEYS, HVALS, HGETALL, HSTRLEN,
                HEXPIRE, HPEXPIRE, HEXPIREAT, HPEXPIREAT, HTTL, HPTTL, HEXPIRETIME, HPEXPIRETIME, HPERSIST,
                HRANDFIELD, HINCRBYFLOAT
        };
        // list
        Protocol.Command[] listCommands = new Protocol.Command[]{
                RPUSH, LPUSH, LLEN, LRANGE, LTRIM, LINDEX, LSET, LREM, LPOP, RPOP, BLPOP, BRPOP, LINSERT, LPOS,
                RPOPLPUSH, BRPOPLPUSH, BLMOVE, LMOVE, LMPOP, BLMPOP, LPUSHX, RPUSHX
        };
        // set
        Protocol.Command[] setCommands = new Protocol.Command[]{
                SADD, SMEMBERS, SREM, SPOP, SMOVE, SCARD, SRANDMEMBER, SINTER, SINTERSTORE, SUNION, SUNIONSTORE,
                SDIFF, SDIFFSTORE, SISMEMBER, SMISMEMBER, SINTERCARD
        };
        // zset
        Protocol.Command[] zsetCommands = new Protocol.Command[]{
                ZADD, ZDIFF, ZDIFFSTORE, ZRANGE, ZREM, ZINCRBY, ZRANK, ZREVRANK, ZREVRANGE, ZRANDMEMBER, ZCARD,
                ZSCORE, ZPOPMAX, ZPOPMIN, ZCOUNT, ZUNION, ZUNIONSTORE, ZINTER, ZINTERSTORE, ZRANGEBYSCORE,
                ZREVRANGEBYSCORE, ZREMRANGEBYRANK, ZREMRANGEBYSCORE, ZLEXCOUNT, ZRANGEBYLEX, ZREVRANGEBYLEX,
                ZREMRANGEBYLEX, ZMSCORE, ZRANGESTORE, ZINTERCARD, ZMPOP, BZMPOP, BZPOPMIN, BZPOPMAX
        };
        // geo
        Protocol.Command[] geoCommands = new Protocol.Command[]{
                GEOADD, GEODIST, GEOHASH, GEOPOS, GEORADIUS, GEORADIUS_RO, GEOSEARCH, GEOSEARCHSTORE,
                GEORADIUSBYMEMBER, GEORADIUSBYMEMBER_RO
        };
        // hyper log
        Protocol.Command[] hyperLogCommands = new Protocol.Command[]{
                PFADD, PFCOUNT, PFMERGE
        };
        // stream
        Protocol.Command[] streamCommands = new Protocol.Command[]{
                XADD, XLEN, XDEL, XTRIM, XRANGE, XREVRANGE, XREAD, XACK, XGROUP, XREADGROUP, XPENDING, XCLAIM,
                XAUTOCLAIM, XINFO
        };
        // other
        Protocol.Command[] otherCommands = new Protocol.Command[]{
                TYPE
        };
        commands.addAll(List.of(keyCommands));
        commands.addAll(List.of(stringCommands));
        commands.addAll(List.of(bitCommands));
        commands.addAll(List.of(hashCommands));
        commands.addAll(List.of(listCommands));
        commands.addAll(List.of(setCommands));
        commands.addAll(List.of(zsetCommands));
        commands.addAll(List.of(geoCommands));
        commands.addAll(List.of(hyperLogCommands));
        commands.addAll(List.of(streamCommands));
        commands.addAll(List.of(otherCommands));
        return commands;
    }
}
