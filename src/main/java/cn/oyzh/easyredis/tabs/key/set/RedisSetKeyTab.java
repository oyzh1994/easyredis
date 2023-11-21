package cn.oyzh.easyredis.tabs.key.set;

import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.redis.key.RedisZSetKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.tabs.key.zset.RedisZSetKeyTabContent;
import cn.oyzh.easyredis.trees.RedisSetKeyTreeItem;
import cn.oyzh.easyredis.trees.RedisZSetKeyTreeItem;
import lombok.NonNull;

/**
 * redis set键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTab extends RedisKeyTab<RedisSetKeyTreeItem> {

    public RedisSetKeyTab(@NonNull RedisSetKeyTreeItem treeItem) {
        super(treeItem);
    }

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
