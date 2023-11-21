package cn.oyzh.easyredis.tabs.key.zset;

import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import lombok.NonNull;

/**
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class ZSetKeyTab extends KeyTab<RedisZSetKeyTreeItem> {

    public ZSetKeyTab(@NonNull RedisZSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisZSetKeyTabContent.fxml";
    }

    @Override
    public ZSetKeyTabContent controller() {
        return (ZSetKeyTabContent) super.controller();
    }

    @Override
    public RedisZSetKey key() {
        return (RedisZSetKey) super.key();
    }
}
