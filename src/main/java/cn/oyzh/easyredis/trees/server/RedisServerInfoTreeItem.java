package cn.oyzh.easyredis.trees.server;

import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import javafx.scene.control.MenuItem;
import lombok.NonNull;

import java.util.List;

/**
 * redis服务信息树节点
 *
 * @author oyzh
 * @since 2023/8/10
 */
public class RedisServerInfoTreeItem extends RedisTreeItem<RedisServerInfoTreeItemValue> {

    /**
     * 父节点
     */
    private final RedisConnectTreeItem parent;

    public RedisServerInfoTreeItem(@NonNull RedisConnectTreeItem parent ) {
        super(parent.getTreeView());
        this.parent = parent;
        this.setValue(new RedisServerInfoTreeItemValue());
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return null;
    }

    /**
     * 显示服务信息
     */
    public void showServerInfo() {
        // EventUtil.fire(RedisEventTypes.REDIS_SERVER_INFO, this.parent.client());
        RedisEventUtil.serverMonitor(this.parent.client());
    }

    @Override
    public void onPrimarySingleClick() {
        this.showServerInfo();
    }
}
