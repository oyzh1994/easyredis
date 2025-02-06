package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * redis服务信息树节点
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class RedisServerInfoTreeItem extends RichTreeItem<RedisServerInfoTreeItemValue> {

    public RedisServerInfoTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisServerInfoTreeItemValue());
    }

    @Override
    public RedisConnectTreeItem parent() {
        return (RedisConnectTreeItem) super.parent();
    }

    public RedisClient client() {
        return this.parent().client();
    }

//    /**
//     * 显示服务信息
//     */
//    public void showServerInfo() {
//        RedisEventUtil.serverMonitor(this.client());
//    }

    @Override
    public void onPrimarySingleClick() {
        RedisEventUtil.server(this.client());
    }

}
