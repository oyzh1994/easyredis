package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisQueryStore;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class RedisQueriesTreeItem extends RichTreeItem<RedisQueriesTreeItemValue> {


    private final RedisQueryStore queryStore = RedisQueryStore.INSTANCE;

    public RedisQueriesTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new RedisQueriesTreeItemValue());
    }

    @Override
    public RedisConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisConnectTreeItem) parent;
    }

    public RedisClient client() {
        return this.parent().client();
    }

    public RedisConnect redisConnect() {
        return this.parent().value();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addQuery = MenuItemHelper.addQuery("12", this::addQuery);
        items.add(addQuery);
        return items;
    }

    private void addQuery() {
        RedisEventUtil.addQuery(this.parent().client());
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            try {
                this.setLoaded(true);
                this.setLoading(true);
                String iid = this.parent().getId();
                List<RedisQuery> queries = this.queryStore.list(iid);
                List<TreeItem<?>> items = new ArrayList<>();
                for (RedisQuery query : queries) {
                    items.add(new RedisQueryTreeItem(query, this.getTreeView()));
                }
                this.setChild(items);
                this.expend();
            } catch (Exception ex) {
                ex.printStackTrace();
                this.setLoaded(false);
            } finally {
                this.setLoading(false);
            }
        }
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public void add(RedisQuery query) {
        this.addChild(new RedisQueryTreeItem(query, this.getTreeView()));
    }

}
