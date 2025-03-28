package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/12
 */
public class RedisKeysMovedEvent extends Event<RedisDatabaseTreeItem>   {

    private int targetDB;

    public int getTargetDB() {
        return targetDB;
    }

    public void setTargetDB(int targetDB) {
        this.targetDB = targetDB;
    }

    public int sourceDB() {
        return this.data().dbIndex();
    }

    public RedisConnect redisConnect() {
        return this.data().redisConnect();
    }
}
