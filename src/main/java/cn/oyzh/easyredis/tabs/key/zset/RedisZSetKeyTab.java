package cn.oyzh.easyredis.tabs.key.zset;

import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.keys.zset.RedisZSetKeyTreeItem;

/**
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisZSetKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    @Override
    protected String url() {
        return "/tabs/key/redisZSetKeyTabContent.fxml";
    }

    @Override
    public RedisZSetKeyTabContent controller() {
        return (RedisZSetKeyTabContent) super.controller();
    }

    @Override
    public RedisZSetKey key() {
        return (RedisZSetKey) super.key();
    }
}
