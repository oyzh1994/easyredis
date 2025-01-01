package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.keys.RedisListKeyTreeItem;

/**
 * redis list键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisListKeyTab extends RedisKeyTab<RedisListKeyTreeItem> {

    public RedisListKeyTab(RedisListKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisListKeyTab.fxml";
    }

    @Override
    public RedisListKeyTabController controller() {
        return (RedisListKeyTabController) super.controller();
    }

}
