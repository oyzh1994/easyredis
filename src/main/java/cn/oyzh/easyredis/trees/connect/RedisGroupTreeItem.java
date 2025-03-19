package cn.oyzh.easyredis.trees.connect;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyredis.domain.RedisConnect;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisConnectStore;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * redis分组键
 *
 * @author oyzh
 * @since 2023/05/12
 */
public class RedisGroupTreeItem extends RichTreeItem<RedisGroupTreeItemValue> implements RedisConnectManager {

    /**
     * 分组对象
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final RedisGroup value;

    /**
     * redis分组储存
     */
    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    /**
     * redis连接储存
     */
    private final RedisConnectStore connectStore = RedisConnectStore.INSTANCE;

    public RedisGroupTreeItem( RedisGroup group,  RichTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new RedisGroupTreeItemValue(this));
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            if (this.value.isExpand()) {
                this.value.setExpand(false);
                this.groupStore.update(this.value);
            }
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            if (!this.value.isExpand()) {
                this.value.setExpand(true);
                this.groupStore.update(this.value);
            }
        });
    }

    @Override
    public RedisConnectTreeView getTreeView() {
        return (RedisConnectTreeView) super.getTreeView();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>(4);
        FXMenuItem addConnect = MenuItemHelper.addConnect("12", this::addConnect);
        FXMenuItem renameGroup = MenuItemHelper.renameGroup("12", this::rename);
        FXMenuItem delGroup = MenuItemHelper.deleteGroup("12", this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(groupName)) {
            return;
        }
        // 检查是否存在
        if (this.groupStore.exist(groupName)) {
            MessageBox.warn(I18nHelper.groupAlreadyExists());
            return;
        }
        // 旧名称
        String oldName = this.value.getName();
        // 修改名称
        this.value.setName(groupName);
        if (this.groupStore.replace(this.value)) {
            this.refresh();
            RedisEventUtil.groupRenamed(groupName, oldName);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip1())) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip2())) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value.getName())) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<RedisConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父节点
            this.parent().addConnectItems(childes);
        }
        // 发送事件
        RedisEventUtil.groupDeleted(this.value.getName());
        // 移除节点
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
//        StageAdapter fxView = StageManager.parseStage(RedisAddConnectController.class, this.window());
//        fxView.setProp("group", this.value);
//        fxView.display();
        RedisEventUtil.showAddConnect(this.value);
    }

    @Override
    public RedisRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (RedisRootTreeItem) treeItem;
    }

    @Override
    public void addConnect( RedisConnect redisConnect) {
        this.addConnectItem(new RedisConnectTreeItem(redisConnect, this.getTreeView()));
    }

    @Override
    public void addConnectItem( RedisConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
               this.connectStore.replace(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems( List<RedisConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem( RedisConnectTreeItem item) {
        // 删除连接
        if (this.connectStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<RedisConnectTreeItem> getConnectItems() {
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.unfilteredChildren()) {
            if (item instanceof RedisConnectTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        if (item instanceof RedisConnectTreeItem connectTreeItem) {
            return !Objects.equals(connectTreeItem.value().getGroupId(), this.value.getGid());
        }
        return false;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof RedisConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }

    public String getGid() {
        return this.value.getGid();
    }

}
