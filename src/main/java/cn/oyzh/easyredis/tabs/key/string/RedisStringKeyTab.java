package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.key.list.RedisListKeyTabContent;
import cn.oyzh.easyredis.trees.RedisListKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisStringKeyTreeItem;
import lombok.NonNull;

/**
 * redis string键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    public RedisStringKeyTab(@NonNull RedisStringKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisStringKeyTabContent.fxml";
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
