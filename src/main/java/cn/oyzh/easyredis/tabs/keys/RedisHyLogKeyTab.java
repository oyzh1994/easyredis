package cn.oyzh.easyredis.tabs.keys;

import cn.oyzh.easyredis.trees.keys.RedisStringKeyTreeItem;

/**
 * redis hyLog键tab
 *
 * @author oyzh
 * @since 2024/05/17
 */
public class RedisHyLogKeyTab extends RedisKeyTab<RedisStringKeyTreeItem> {

    public RedisHyLogKeyTab(RedisStringKeyTreeItem treeItem) {
        super(treeItem);
    }

    @Override
    protected String url() {
        return "/tabs/keys/redisHyLogKeyTab.fxml";
    }

    @Override
    public RedisHylogKeyTabController controller() {
        return (RedisHylogKeyTabController) super.controller();
    }

}
