package cn.oyzh.easyredis.tabs.key.hylog;

import cn.oyzh.easyredis.redis.key.RedisHyperLogLogKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.hylog.RedisHyperLogLogKeyTreeItem;
import lombok.NonNull;

/**
 * redis hyperLogLog键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisHyperLogLogKeyTab extends RedisKeyTab<RedisHyperLogLogKeyTreeItem> {

    public RedisHyperLogLogKeyTab(@NonNull RedisHyperLogLogKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisHyperLogLogKeyTabContent.fxml";
    }

    @Override
    public RedisHyperLogLogKeyTabContent controller() {
        return (RedisHyperLogLogKeyTabContent) super.controller();
    }

    @Override
    public RedisHyperLogLogKey key() {
        return (RedisHyperLogLogKey) super.key();
    }
}
