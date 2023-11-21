package cn.oyzh.easyredis.tabs.key.geo;

import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.zset.RedisZSetKeyTreeItem;
import lombok.NonNull;

/**
 * redis geo键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisGEOKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    public RedisGEOKeyTab(@NonNull RedisZSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisGEOKeyTabContent.fxml";
    }

    @Override
    public RedisGEOKeyTabContent controller() {
        return (RedisGEOKeyTabContent) super.controller();
    }

    @Override
    public RedisZSetKey key() {
        return (RedisZSetKey) super.key();
    }
}
