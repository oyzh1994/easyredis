package cn.oyzh.easyredis.tabs.key.string;

import cn.oyzh.easyredis.redis.key.RedisStringKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.string.RedisStringKeyTreeItem;
import lombok.NonNull;

/**
 * redis string键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class StringKeyTab extends KeyTab<RedisStringKeyTreeItem> {

    public StringKeyTab(@NonNull RedisStringKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisStringKeyTabContent.fxml";
    }

    @Override
    public StringKeyTabContent controller() {
        return (StringKeyTabContent) super.controller();
    }

    @Override
    public RedisStringKey key() {
        return (RedisStringKey) super.key();
    }
}
