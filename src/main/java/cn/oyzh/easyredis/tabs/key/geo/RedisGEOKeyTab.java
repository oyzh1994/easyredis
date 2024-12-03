package cn.oyzh.easyredis.tabs.key.geo;

import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.keys.zset.RedisZSetKeyTreeItem;

/**
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisGEOKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    @Override
    protected String url() {
        return "/tabs/key/redisGEOKeyTabContent.fxml";
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
