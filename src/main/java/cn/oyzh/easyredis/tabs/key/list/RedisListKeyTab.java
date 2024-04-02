package cn.oyzh.easyredis.tabs.key.list;

import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;

/**
 * redis list键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisListKeyTab extends RedisKeyTab<RedisListKeyTreeItem> {

    @Override
    protected String url() {
        return  "/tabs/key/redisListKeyTabContent.fxml";
    }

    @Override
    public RedisListKeyTabContent controller() {
        return (RedisListKeyTabContent) super.controller();
    }

    @Override
    public RedisListKey key() {
        return (RedisListKey) super.key();
    }
}
