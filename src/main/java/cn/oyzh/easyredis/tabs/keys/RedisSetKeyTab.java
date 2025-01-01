package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.keys.RedisSetKeyTreeItem;

/**
 * redis set键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisSetKeyTab extends RedisKeyTab<RedisSetKeyTreeItem> {

    public RedisSetKeyTab(RedisSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisSetKeyTab.fxml";
    }

    @Override
    public RedisSetKeyTabController controller() {
        return (RedisSetKeyTabController) super.controller();
    }

}
