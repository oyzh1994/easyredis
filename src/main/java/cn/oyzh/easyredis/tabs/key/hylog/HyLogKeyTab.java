package cn.oyzh.easyredis.tabs.key.hylog;

import cn.oyzh.easyredis.redis.key.RedisHyperLogLogKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.hylog.RedisHyperLogLogKeyTreeItem;
import lombok.NonNull;

/**
 * redis hyperLogLog键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class HyLogKeyTab extends KeyTab<RedisHyperLogLogKeyTreeItem> {

    public HyLogKeyTab(@NonNull RedisHyperLogLogKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisHyperLogLogKeyTabContent.fxml";
    }

    @Override
    public HyLogKeyTabContent controller() {
        return (HyLogKeyTabContent) super.controller();
    }

    @Override
    public RedisHyperLogLogKey key() {
        return (RedisHyperLogLogKey) super.key();
    }
}
