package cn.oyzh.easyredis.tabs.key.list;

import cn.oyzh.easyredis.redis.key.RedisListKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.list.RedisListKeyTreeItem;
import lombok.NonNull;

/**
 * redis list键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class ListKeyTab extends KeyTab<RedisListKeyTreeItem> {

    public ListKeyTab(@NonNull RedisListKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisListKeyTabContent.fxml";
    }

    @Override
    public ListKeyTabContent controller() {
        return (ListKeyTabContent) super.controller();
    }

    @Override
    public RedisListKey key() {
        return (RedisListKey) super.key();
    }
}
