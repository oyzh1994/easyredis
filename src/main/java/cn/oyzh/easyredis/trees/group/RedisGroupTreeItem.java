package cn.oyzh.easyredis.trees.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyredis.controller.info.RedisInfoAddController;
import cn.oyzh.easyredis.domain.RedisGroup;
import cn.oyzh.easyredis.domain.RedisInfo;
import cn.oyzh.easyredis.event.RedisEventUtil;
import cn.oyzh.easyredis.redis.RedisConnectManager;
import cn.oyzh.easyredis.store.RedisGroupStore;
import cn.oyzh.easyredis.store.RedisInfoStore;
import cn.oyzh.easyredis.trees.RedisTreeItem;
import cn.oyzh.easyredis.trees.RedisTreeView;
import cn.oyzh.easyredis.trees.connect.RedisConnectTreeItem;
import cn.oyzh.easyredis.trees.root.RedisRootTreeItem;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
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
//@Slf4j
public class RedisGroupTreeItem extends RedisTreeItem<RedisGroupTreeItemValue> implements RedisConnectManager {

    /**
     * 分组对象
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final RedisGroup value;

    /**
     * redis信息储存
     */
    private final RedisInfoStore infoStore = RedisInfoStore.INSTANCE;

    /**
     * redis分组储存
     */
    private final RedisGroupStore groupStore = RedisGroupStore.INSTANCE;

    public RedisGroupTreeItem(@NonNull RedisGroup group, @NonNull RedisTreeView treeView) {
        super(treeView);
        this.value = group;
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        this.setValue(new RedisGroupTreeItemValue(this));
        // 监听键变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            RedisEventUtil.treeChildChanged();
            this.flushLocal();
        });
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(false);
            this.groupStore.update(this.value);
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(true);
            this.groupStore.update(this.value);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem addConnect = MenuItemExt.newItem("添加连接", new SVGGlyph("/font/add.svg", "12"), "添加redis连接", this::addConnect);
        MenuItem renameGroup = MenuItemExt.newItem("分组更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改分组名称(快捷键f2)", this::rename);
        MenuItem delGroup = MenuItemExt.newItem("删除分组", new SVGGlyph("/font/delete.svg", "12"), "删除此分组", this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt("请输入新的分组名称", this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StrUtil.isBlank(groupName)) {
            return;
        }
        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(groupName);
        if (this.groupStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn("此分组已存在！");
            return;
        }
        // 修改名称
        if (this.groupStore.update(this.value)) {
            this.getValue().name(groupName);
        } else {
            MessageBox.warn("修改分组名称失败！");
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm("确定删除此分组？")) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm("确定删除此分组？(连接将移动到根键)")) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value)) {
            MessageBox.warn("删除分组失败！");
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<RedisConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父键
            this.parent().addConnectItems(childes);
        }
        // 移除键
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageWrapper fxView = StageUtil.parseStage(RedisInfoAddController.class, this.parent().window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父键
     *
     * @return redis根键
     */
    public RedisRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (RedisRootTreeItem) treeItem;
    }

    @Override
    public void addConnect(@NonNull RedisInfo redisInfo) {
        this.addConnectItem(new RedisConnectTreeItem(redisInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem(@NonNull RedisConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                this.infoStore.update(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems(@NonNull List<RedisConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem(@NonNull RedisConnectTreeItem item) {
        // 删除连接
        if (this.infoStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<RedisConnectTreeItem> getConnectItems() {
        List<RedisConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
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
}
