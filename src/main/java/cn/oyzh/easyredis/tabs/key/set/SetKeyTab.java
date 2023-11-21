package cn.oyzh.easyredis.tabs.key.set;

import cn.oyzh.easyredis.redis.key.RedisSetKey;
import cn.oyzh.easyredis.tabs.key.KeyTab;
import cn.oyzh.easyredis.trees.set.RedisSetKeyTreeItem;
import lombok.NonNull;

/**
 * redis set键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class SetKeyTab extends KeyTab<RedisSetKeyTreeItem> {

    public SetKeyTab(@NonNull RedisSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return  "/tabs/key/redisSetKeyTabContent.fxml";
    }

    @Override
    public SetKeyTabContent controller() {
        return (SetKeyTabContent) super.controller();
    }

    @Override
    public RedisSetKey key() {
        return (RedisSetKey) super.key();
    }
}
