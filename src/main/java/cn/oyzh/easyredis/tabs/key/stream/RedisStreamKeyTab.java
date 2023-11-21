package cn.oyzh.easyredis.tabs.key.stream;

import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.key.string.RedisStringKeyTabContent;
import cn.oyzh.easyredis.trees.RedisStreamKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisStringKeyTreeItem;
import lombok.NonNull;

/**
 * redis stream键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStreamKeyTab extends RedisKeyTab<RedisStreamKeyTreeItem> {

    public RedisStreamKeyTab(@NonNull RedisStreamKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisStreamKeyTabContent.fxml";
    }

    @Override
    public RedisStreamKeyTabContent controller() {
        return (RedisStreamKeyTabContent) super.controller();
    }

    @Override
    public RedisStreamKey key() {
        return (RedisStreamKey) super.key();
    }
}
