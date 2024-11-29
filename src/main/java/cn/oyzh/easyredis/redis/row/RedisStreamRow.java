package cn.oyzh.easyredis.redis.row;

import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.easyredis.redis.RedisRow;
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
        return JSONUtil.toJson(entry.getFields());
    }

    public StreamEntryID getStreamId() {
        return this.entry.getID();
    }
}

