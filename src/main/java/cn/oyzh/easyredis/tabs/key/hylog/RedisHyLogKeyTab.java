package cn.oyzh.easyredis.tabs.key.hylog;

import cn.oyzh.easyredis.redis.key.RedisHyLogKey;
import cn.oyzh.easyredis.tabs.key.RedisKeyTab;
import cn.oyzh.easyredis.trees.hylog.RedisHyLogKeyTreeItem;
import lombok.NonNull;

/**
 * redis hyperLogLog键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisHyLogKeyTab extends RedisKeyTab<RedisHyLogKeyTreeItem> {

    public RedisHyLogKeyTab(@NonNull RedisHyLogKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisHyLogKeyTabContent.fxml";
    }

    @Override
    public RedisHyLogKeyTabContent controller() {
        return (RedisHyLogKeyTabContent) super.controller();
    }

    @Override
    public RedisHyLogKey key() {
        return (RedisHyLogKey) super.key();
    }
}
