package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;

/**
 * redis string键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisStringKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    public RedisStringKeyTab(RedisStringKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisStringKeyTab.fxml";
    }

    @Override
    public RedisStringKeyTabController controller() {
        return (RedisStringKeyTabController) super.controller();
    }

}
