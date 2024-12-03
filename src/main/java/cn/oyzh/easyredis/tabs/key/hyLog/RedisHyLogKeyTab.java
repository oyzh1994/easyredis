package cn.oyzh.easyredis.tabs.key.hyLog;

import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.keys.string.RedisStringKeyTreeItem;

/**
 * redis hyLog键tab
 *
 * @author oyzh
 * @since 2024/05/17
 */
public class RedisHyLogKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    @Override
    protected String url() {
        return "/tabs/key/redisHyLogKeyTabContent.fxml";
    }

    @Override
    public RedisHylogKeyTabContent controller() {
        return (RedisHylogKeyTabContent) super.controller();
    }

    @Override
    public RedisStringKey key() {
        return (RedisStringKey) super.key();
    }
}
