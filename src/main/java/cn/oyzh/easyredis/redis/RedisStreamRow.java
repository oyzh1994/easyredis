package cn.oyzh.easyredis.redis;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.resps.StreamEntry;

/**
 * redis stream行
 *
 * @author oyzh
 * @since 2023/6/16
 */
public class RedisStreamRow extends RedisRow {

    /**
     * 消息内容
     */
    @Getter
    private final StreamEntry entry;

    public RedisStreamRow(StreamEntry entry) {
        this.entry = entry;
    }

    public String getId() {
        return this.entry.getID().toString();
    }

    public String getValue() {
        return JSON.toJSONString(entry.getFields());
    }

    // public String getValuePretty() {
    //     return JSON.toJSONString(entry.getFields(), true);
    // }

    public StreamEntryID getStreamId() {
        return this.entry.getID();
    }
}

