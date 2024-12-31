package cn.oyzh.easyredis.event.key;

import cn.oyzh.easyredis.trees.connect.RedisDatabaseTreeItem;
import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/5/17
 */
public class RedisZSetReverseViewEvent extends Event<RedisZSetKeyTreeItem> {

    public RedisDatabaseTreeItem dbItem() {
        return this.data().getTreeView().dbItem();
    }

}
