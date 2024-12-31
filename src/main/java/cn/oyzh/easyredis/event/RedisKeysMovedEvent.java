package cn.oyzh.easyredis.event;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/12/12
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RedisKeysMovedEvent extends Event<RedisDatabaseTreeItem>   {

    private int targetDB;

    public int sourceDB() {
        return this.data().dbIndex();
    }

    public RedisConnect redisConnect() {
        return this.data().redisConnect();
    }
}
