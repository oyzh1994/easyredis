package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.keys.string.RedisStringKeyTreeItem;

/**
 * redis string键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    @Override
    protected String url() {
        return "/tabs/key/redisStringKeyTabContent.fxml";
    }

    @Override
    public RedisStringKeyTabContent controller() {
        return (RedisStringKeyTabContent) super.controller();
    }

    @Override
    public RedisStringKey key() {
        return (RedisStringKey) super.key();
    }
}
