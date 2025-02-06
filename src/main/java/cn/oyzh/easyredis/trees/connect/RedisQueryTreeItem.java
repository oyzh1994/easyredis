package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisQuery;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisClient;
import cn.oyzh.easyredis.store.RedisQueryStore;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class RedisQueryTreeItem extends RichTreeItem<RedisQueryTreeItemValue> {

    @Getter
    @Accessors(fluent = true)
    private final RedisQuery value;

    private final RedisQueryStore queryStore = RedisQueryStore.INSTANCE;

    public RedisQueryTreeItem(RedisQuery query, RichTreeView treeView) {
        super(treeView);
        this.value = query;
        this.setValue(new RedisQueryTreeItemValue(this));
    }

    @Override
    public RedisQueriesTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (RedisQueriesTreeItem) parent;
    }

    public RedisClient client() {
        return this.parent().client();
    }

    public RedisConnect redisConnect() {
        return this.parent().redisConnect();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openQuery = MenuItemHelper.openQuery("12", this::loadChild);
        FXMenuItem renameQuery = MenuItemHelper.renameQuery("12", this::rename);
        FXMenuItem deleteQuery = MenuItemHelper.deleteQuery("12", this::delete);
        items.add(openQuery);
        items.add(renameQuery);
        items.add(deleteQuery);
        return items;
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.deleteQuery() + "[" + this.value.getName() + "]?")) {
            if (this.queryStore.delete(this.value)) {
                super.remove();
                RedisEventUtil.queryDeleted(this.value);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String queryName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (queryName == null || Objects.equals(queryName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(queryName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }
        this.value.setName(queryName);
        // 修改名称
        if (this.queryStore.update(this.value)) {
            this.setValue(new RedisQueryTreeItemValue(this));
            RedisEventUtil.queryRenamed(this.value);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void loadChild() {
        RedisEventUtil.openQuery(this.client(), this.value);
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }
}
