package cn.oyzh.easyredis.tabs.key.stream;

import cn.oyzh.easyredis.redis.key.RedisStreamKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.stream.RedisStreamKeyTreeItem;
import lombok.NonNull;

/**
 * redis stream键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class StreamKeyTab extends KeyTab<RedisStreamKeyTreeItem> {

    public StreamKeyTab(@NonNull RedisStreamKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisStreamKeyTabContent.fxml";
    }

    @Override
    public StreamKeyTabContent controller() {
        return (StreamKeyTabContent) super.controller();
    }

    @Override
    public RedisStreamKey key() {
        return (RedisStreamKey) super.key();
    }
}
