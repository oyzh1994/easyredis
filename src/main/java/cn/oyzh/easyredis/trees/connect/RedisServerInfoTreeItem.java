package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openInfo = MenuItemHelper.openInfo("12", this::loadChild);
        items.add(openInfo);
        return items;
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

    public RedisConnect redisConnect() {
        return this.client().redisConnect();
    }
}
