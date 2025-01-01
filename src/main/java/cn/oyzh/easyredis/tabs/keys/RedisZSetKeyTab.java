package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.keys.RedisZSetKeyTreeItem;

/**
 * redis zset键tab
 *
 * @author oyzh
 * @since 2023/11/21
 */
public class RedisZSetKeyTab extends RedisKeyTab<RedisZSetKeyTreeItem> {

    public RedisZSetKeyTab(RedisZSetKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisZSetKeyTab.fxml";
    }

    @Override
    public RedisZSetKeyTabController controller() {
        return (RedisZSetKeyTabController) super.controller();
    }

}
