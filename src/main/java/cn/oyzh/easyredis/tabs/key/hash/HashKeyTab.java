package cn.oyzh.easyredis.tabs.key.hash;

import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;
import lombok.NonNull;

/**
 * redis hash键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class HashKeyTab extends KeyTab<RedisHashKeyTreeItem> {

    public HashKeyTab(@NonNull RedisHashKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisHashKeyTabContent.fxml";
    }

    @Override
    public HashKeyTabContent controller() {
        return (HashKeyTabContent) super.controller();
    }

    @Override
    public RedisHashKey key() {
        return (RedisHashKey) super.key();
    }
}
