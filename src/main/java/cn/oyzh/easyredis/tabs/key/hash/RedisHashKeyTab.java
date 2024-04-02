package cn.oyzh.easyredis.tabs.key.hash;

import cn.oyzh.easyredis.redis.key.RedisHashKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.hash.RedisHashKeyTreeItem;

/**
 * redis hash键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisHashKeyTab extends RedisKeyTab<RedisHashKeyTreeItem> {

    @Override
    protected String url() {
        return "/tabs/key/redisHashKeyTabContent.fxml";
    }

    @Override
    public RedisHashKeyTabContent controller() {
        return (RedisHashKeyTabContent) super.controller();
    }

    @Override
    public RedisHashKey key() {
        return (RedisHashKey) super.key();
    }
}
