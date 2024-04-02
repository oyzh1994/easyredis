package cn.oyzh.easyredis.tabs.key.set;

import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;

/**
 * redis set键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTab extends RedisKeyTab<RedisSetKeyTreeItem> {

    @Override
    protected String url() {
        return  "/tabs/key/redisSetKeyTabContent.fxml";
    }

    @Override
    public RedisSetKeyTabContent controller() {
        return (RedisSetKeyTabContent) super.controller();
    }

    @Override
    public RedisSetKey key() {
        return (RedisSetKey) super.key();
    }
}
