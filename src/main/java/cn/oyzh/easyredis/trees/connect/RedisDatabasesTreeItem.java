package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.node.NodeLifeCycle;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * redis数据库树节点
 *
 * @author oyzh
 * @since 2023/07/12
 */
public class RedisDatabasesTreeItem extends RichTreeItem<RedisDatabasesTreeItemValue> implements NodeLifeCycle {

    public RedisDatabasesTreeItem(RedisConnectTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        this.setValue(new RedisDatabasesTreeItemValue(this));
    }

    @Override
    public RedisConnectTreeItem parent() {
        return (RedisConnectTreeItem) super.parent();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        // 卸载
        FXMenuItem unload = MenuItemHelper.unload("12", this::unloadChild);
        items.add(unload);
        return items;
    }

    /**
     * 取消加载
     */
    public void unloadChild() {
        this.clearChild();
        this.setLoaded(false);
    }

    /**
     * 获取redis客户端
     *
     * @return redis客户端
     */
    public RedisClient client() {
        return this.parent().client();
    }

    public int databases() {
        return this.client().databases();
    }

    @Override
    public RedisConnectTreeView getTreeView() {
        return (RedisConnectTreeView) super.getTreeView();
    }

    @Override
    public void loadChild() {
        if (!this.isLoaded() && !this.isLoading()) {
            try {
                this.setLoaded(true);
                this.setLoading(true);
                // cluster集群模式
                if (this.client().isClusterMode()) {
                    this.setChild(new RedisDatabaseTreeItem(null, this.getTreeView()));
                } else {// 正常模式
                    int databases = this.databases();
                    List<TreeItem<?>> items = new ArrayList<>(databases);
                    for (int dbIndex = 0; dbIndex < databases; dbIndex++) {
                        items.add(new RedisDatabaseTreeItem(dbIndex, this.getTreeView()));
                    }
                    this.setChild(items);
                }
                this.expend();
            } catch (Exception ex) {
                this.setLoaded(false);
                ex.printStackTrace();
                JulLog.warn("loadChild error", ex);
            } finally {
                this.setLoading(false);
            }
        }
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.startWaiting(this::loadChild);
        } else {
            super.onPrimaryDoubleClick();
        }
    }

}
